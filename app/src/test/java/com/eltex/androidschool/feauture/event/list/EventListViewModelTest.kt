package com.eltex.androidschool.feauture.event.list

import com.eltex.androidschool.domain.LoadingState
import com.eltex.androidschool.feauture.TrampolineSchedulerProvider
import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class EventListViewModelTest {
    private lateinit var viewModel: EventListViewModel

    @After
    fun tearDown() {
        viewModel.onCleared()
    }


    @Test
    fun `load success - events are mapped and status becomes Idle`() {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.just(listOf(event(1L, "First"), event(2L, "Second")))
            },
            schedulers = TrampolineSchedulerProvider,
        )

        assertEquals(LoadingState.Idle, viewModel.state.status)
        assertEquals(2, viewModel.state.events?.size)
        assertEquals("First", viewModel.state.events?.get(0)?.content)
        assertEquals("Second", viewModel.state.events?.get(1)?.content)
    }

    @Test
    fun `load error - status becomes Error`() {
        val error = RuntimeException("network error")
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.error(error)
            },
            schedulers = TrampolineSchedulerProvider,
        )

        assertEquals(LoadingState.Error(error), viewModel.state.status)
    }


    @Test
    fun `like success - liked event is updated in list`() {
        val original = event(1L, likedByMe = false, likes = 5)
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.just(listOf(original))
                likeResult = Single.just(original.copy(likedByMe = true, likes = 6))
            },
            schedulers = TrampolineSchedulerProvider,
        )

        viewModel.accept(EventMessage.Like(id = 1L))

        val updated = viewModel.state.events?.find { it.id == 1L }
        assertEquals(true, updated?.likedByMe)
        assertEquals(6, updated?.likes)
    }

    @Test
    fun `like error - events in state are unchanged`() {
        val original = event(1L, likedByMe = false, likes = 5)
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.just(listOf(original))
                likeResult = Single.error(RuntimeException("like failed"))
            },
            schedulers = TrampolineSchedulerProvider,
        )

        viewModel.accept(EventMessage.Like(id = 1L))

        val unchanged = viewModel.state.events?.find { it.id == 1L }
        assertEquals(false, unchanged?.likedByMe)
        assertEquals(5, unchanged?.likes)
    }


    @Test
    fun `participate success - participated event is updated in list`() {
        val original = event(1L, participatedByMe = false, participants = 3)
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.just(listOf(original))
                participateResult = Single.just(original.copy(participatedByMe = true, participants = 4))
            },
            schedulers = TrampolineSchedulerProvider,
        )

        viewModel.accept(EventMessage.Participate(id = 1L))

        val updated = viewModel.state.events?.find { it.id == 1L }
        assertEquals(true, updated?.participatedByMe)
        assertEquals(4, updated?.participants)
    }

    @Test
    fun `participate error - events in state are unchanged`() {
        val original = event(1L, participatedByMe = false, participants = 3)
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.just(listOf(original))
                participateResult = Single.error(RuntimeException("participate failed"))
            },
            schedulers = TrampolineSchedulerProvider,
        )

        viewModel.accept(EventMessage.Participate(id = 1L))

        val unchanged = viewModel.state.events?.find { it.id == 1L }
        assertEquals(false, unchanged?.participatedByMe)
        assertEquals(3, unchanged?.participants)
    }


    @Test
    fun `saveEvent new - event is prepended to existing list`() {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.just(listOf(event(1L)))
                saveResult = Single.just(event(99L, "New"))
            },
            schedulers = TrampolineSchedulerProvider,
        )

        viewModel.accept(EventMessage.AddEvent(id = 0L, text = "New"))

        assertEquals(2, viewModel.state.events?.size)
        assertEquals(99L, viewModel.state.events?.first()?.id)
    }

    @Test
    fun `saveEvent edit - existing event is replaced in list`() {
        val original = event(1L, "Old content")
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.just(listOf(original))
                saveResult = Single.just(original.copy(content = "New content"))
            },
            schedulers = TrampolineSchedulerProvider,
        )

        viewModel.accept(EventMessage.AddEvent(id = 1L, text = "New content"))

        assertEquals(1, viewModel.state.events?.size)
        assertEquals("New content", viewModel.state.events?.first()?.content)
    }

    @Test
    fun `saveEvent error - events in state are unchanged`() {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.just(listOf(event(1L)))
                saveResult = Single.error(RuntimeException("save failed"))
            },
            schedulers = TrampolineSchedulerProvider,
        )

        viewModel.accept(EventMessage.AddEvent(id = 0L, text = "content"))

        assertEquals(1, viewModel.state.events?.size)
        assertEquals(1L, viewModel.state.events?.first()?.id)
    }


    @Test
    fun `deleteEvent success - event is removed from list`() {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.just(listOf(event(1L), event(2L)))
                deleteResult = Completable.complete()
            },
            schedulers = TrampolineSchedulerProvider,
        )

        viewModel.accept(EventMessage.Delete(id = 1L))

        assertEquals(listOf(2L), viewModel.state.events?.map { it.id })
    }

    @Test
    fun `deleteEvent error - events in state are unchanged`() {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = Single.just(listOf(event(1L), event(2L)))
                deleteResult = Completable.error(RuntimeException("delete failed"))
            },
            schedulers = TrampolineSchedulerProvider,
        )

        viewModel.accept(EventMessage.Delete(id = 1L))

        assertEquals(listOf(1L, 2L), viewModel.state.events?.map { it.id })
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
}

private class FakeEventRepository : EventRepository {
    var eventsResult: Single<List<Event>> = Single.just(emptyList())
    var likeResult: Single<Event> = Single.error(NotImplementedError())
    var participateResult: Single<Event> = Single.error(NotImplementedError())
    var saveResult: Single<Event> = Single.error(NotImplementedError())
    var deleteResult: Completable = Completable.error(NotImplementedError())

    override fun getEvents(): Single<List<Event>> = eventsResult
    override fun likeById(id: Long, likedByMe: Boolean): Single<Event> = likeResult
    override fun participateById(id: Long, participatedByMe: Boolean): Single<Event> = participateResult
    override fun saveEvent(id: Long, content: String): Single<Event> = saveResult
    override fun deleteById(id: Long): Completable = deleteResult
}