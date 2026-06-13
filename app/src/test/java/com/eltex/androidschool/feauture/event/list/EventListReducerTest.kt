package com.eltex.androidschool.feauture.event.list

import arrow.core.left
import arrow.core.right
import com.eltex.androidschool.domain.AppException
import com.eltex.androidschool.domain.LoadingState
import com.eltex.androidschool.feauture.event.domain.Event
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class EventListReducerTest {

    private val reducer = EventListReducer()

    @Test
    fun `LoadInitial - sets Loading and emits LoadInitial effect`() {
        val result = reducer.reduce(EventListState(), EventMessage.LoadInitial)

        assertEquals(LoadingState.Loading, result.newState.status)
        assertEquals(setOf(EventEffect.LoadInitial(5)), result.effects)
    }

    @Test
    fun `Retry - behaves like LoadInitial`() {
        val result = reducer.reduce(EventListState(), EventMessage.Retry)

        assertEquals(LoadingState.Loading, result.newState.status)
        assertEquals(setOf(EventEffect.LoadInitial(5)), result.effects)
    }

    @Test
    fun `LoadInitialResult success - maps events and becomes Idle`() {
        val events = listOf(event(2L), event(1L))

        val result = reducer.reduce(
            EventListState(status = LoadingState.Loading),
            EventMessage.LoadInitialResult(events.right()),
        )

        assertEquals(LoadingState.Idle, result.newState.status)
        assertEquals(events.map(EventUiModel::fromEvent), result.newState.events)
        assertTrue(result.effects.isEmpty())
    }

    @Test
    fun `LoadInitialResult failure - becomes Error`() {
        val error = AppException.NetworkException()

        val result = reducer.reduce(
            EventListState(status = LoadingState.Loading),
            EventMessage.LoadInitialResult(error.left()),
        )

        assertEquals(LoadingState.Error(error), result.newState.status)
    }

    @Test
    fun `LoadNextPage - with no events - does nothing`() {
        val result = reducer.reduce(EventListState(), EventMessage.LoadNextPage)

        assertEquals(EventListState(), result.newState)
        assertTrue(result.effects.isEmpty())
    }

    @Test
    fun `LoadNextPage - with events - requests page before last id`() {
        val state = EventListState(events = listOf(ui(event(5L)), ui(event(3L))))

        val result = reducer.reduce(state, EventMessage.LoadNextPage)

        assertEquals(LoadingState.Loading, result.newState.status)
        assertEquals(setOf(EventEffect.LoadNextPage(id = 3L, size = 5)), result.effects)
    }

    @Test
    fun `LoadNextPageResult success - appends events`() {
        val state = EventListState(
            events = listOf(ui(event(5L))),
            status = LoadingState.Loading,
        )
        val nextPage = listOf(event(4L), event(3L))

        val result = reducer.reduce(state, EventMessage.LoadNextPageResult(nextPage.right()))

        assertEquals(
            listOf(ui(event(5L))) + nextPage.map(EventUiModel::fromEvent),
            result.newState.events,
        )
        assertEquals(LoadingState.Idle, result.newState.status)
    }

    @Test
    fun `Like - optimistically toggles and emits Like effect with original values`() {
        val state = EventListState(events = listOf(ui(event(1L, likedByMe = false, likes = 5))))

        val result = reducer.reduce(state, EventMessage.Like(id = 1L, liked = false))

        val updated = result.newState.events?.single()
        assertEquals(true, updated?.likedByMe)
        assertEquals(6, updated?.likes)
        assertEquals(
            setOf(
                EventEffect.Like(
                    liked = false,
                    data = LikeData(id = 1L, originalLikedByMe = false, originalLikes = 5),
                )
            ),
            result.effects,
        )
    }

    @Test
    fun `LikeError - reverts to original values and emits Error`() {
        val error = AppException.Forbidden()
        val state = EventListState(events = listOf(ui(event(1L, likedByMe = true, likes = 6))))

        val result = reducer.reduce(
            state,
            EventMessage.LikeError(
                eventId = 1L,
                originalLikedByMe = false,
                originalLikes = 5,
                error = error,
            ),
        )

        val reverted = result.newState.events?.single()
        assertEquals(false, reverted?.likedByMe)
        assertEquals(5, reverted?.likes)
        assertEquals(setOf(EventEffect.Error(error)), result.effects)
    }

    @Test
    fun `Participate - optimistically toggles and emits Participate effect`() {
        val state = EventListState(
            events = listOf(ui(event(1L, participatedByMe = false, participants = 3)))
        )

        val result = reducer.reduce(state, EventMessage.Participate(id = 1L, participated = false))

        val updated = result.newState.events?.single()
        assertEquals(true, updated?.participatedByMe)
        assertEquals(4, updated?.participants)
        assertEquals(
            setOf(
                EventEffect.Participate(
                    participated = false,
                    data = ParticipateData(
                        id = 1L,
                        originalParticipatedByMe = false,
                        originalParticipants = 3,
                    ),
                )
            ),
            result.effects,
        )
    }

    @Test
    fun `Delete - removes event and emits Delete effect`() {
        val toDelete = ui(event(1L))
        val state = EventListState(events = listOf(toDelete, ui(event(2L))))

        val result = reducer.reduce(state, EventMessage.Delete(id = 1L))

        assertEquals(listOf(2L), result.newState.events?.map { it.id })
        assertEquals(setOf(EventEffect.Delete(toDelete)), result.effects)
    }

    @Test
    fun `DeleteError - reinserts original in id order and emits Error`() {
        val error = AppException.Forbidden()
        val original = ui(event(2L))
        val state = EventListState(events = listOf(ui(event(3L)), ui(event(1L))))

        val result = reducer.reduce(state, EventMessage.DeleteError(original = original, error = error))

        assertEquals(listOf(3L, 2L, 1L), result.newState.events?.map { it.id })
        assertEquals(setOf(EventEffect.Error(error)), result.effects)
    }

    @Test
    fun `AddEvent new - emits Save and ScrollTo`() {
        val result = reducer.reduce(EventListState(), EventMessage.AddEvent(id = 0L, text = "New"))

        assertEquals(
            setOf(EventEffect.Save(id = 0L, content = "New"), EventEffect.ScrollTo(0)),
            result.effects,
        )
    }

    @Test
    fun `AddEvent edit - emits only Save`() {
        val result = reducer.reduce(EventListState(), EventMessage.AddEvent(id = 7L, text = "Edit"))

        assertEquals(setOf(EventEffect.Save(id = 7L, content = "Edit")), result.effects)
    }

    @Test
    fun `AddEventResult success new - prepends event`() {
        val state = EventListState(events = listOf(ui(event(1L))))
        val saved = event(99L, "New")

        val result = reducer.reduce(state, EventMessage.AddEventResult(saved.right()))

        assertEquals(listOf(99L, 1L), result.newState.events?.map { it.id })
    }

    @Test
    fun `AddEventResult success edit - replaces existing event`() {
        val state = EventListState(events = listOf(ui(event(1L, content = "Old"))))
        val saved = event(1L, content = "New")

        val result = reducer.reduce(state, EventMessage.AddEventResult(saved.right()))

        assertEquals(1, result.newState.events?.size)
        assertEquals("New", result.newState.events?.single()?.content)
    }

    @Test
    fun `AddEventResult failure - emits Error and keeps events`() {
        val error = AppException.Forbidden()
        val state = EventListState(events = listOf(ui(event(1L))))

        val result = reducer.reduce(state, EventMessage.AddEventResult(error.left()))

        assertEquals(state.events, result.newState.events)
        assertEquals(setOf(EventEffect.Error(error)), result.effects)
    }

    @Test
    fun `Share - emits Share effect with content`() {
        val state = EventListState(events = listOf(ui(event(1L, content = "Hello"))))

        val result = reducer.reduce(state, EventMessage.Share(id = 1L))

        assertEquals(setOf(EventEffect.Share("Hello")), result.effects)
    }

    @Test
    fun `EditEvent - emits EditEvent effect`() {
        val target = ui(event(1L))

        val result = reducer.reduce(EventListState(), EventMessage.EditEvent(event = target))

        assertEquals(setOf(EventEffect.EditEvent(target)), result.effects)
    }

    private fun event(
        id: Long,
        content: String = "content",
        likedByMe: Boolean = false,
        likes: Int = 0,
        participatedByMe: Boolean = false,
        participants: Int = 0,
    ) = Event(
        id = id,
        content = content,
        published = Instant.EPOCH,
        datetime = Instant.EPOCH,
        likedByMe = likedByMe,
        likes = likes,
        participatedByMe = participatedByMe,
        participants = participants,
    )

    private fun ui(event: Event) = EventUiModel.fromEvent(event)
}