package com.eltex.androidschool.feauture.event.list

import com.eltex.androidschool.domain.LoadingState
import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class EventListViewModelTest {

    private lateinit var viewModel: EventListViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `load success - events are mapped and status becomes Idle`() {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = { listOf(event(1L, "First"), event(2L, "Second")) }
            },
            computationDispatcher = testDispatcher,
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
                eventsResult = { throw error }
            },
            computationDispatcher = testDispatcher,
        )

        val status = viewModel.state.status
        assertTrue(status is LoadingState.Error)
        assertEquals("network error", (status as LoadingState.Error).value.message)
    }


    @Test
    fun `like success - liked event is updated in list`() {
        val original = event(1L, likedByMe = false, likes = 5)
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = { listOf(original) }
                likeResult = { original.copy(likedByMe = true, likes = 6) }
            },
            computationDispatcher = testDispatcher,
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
                eventsResult = { listOf(original) }
                likeResult = { throw RuntimeException("like failed") }
            },
            computationDispatcher = testDispatcher,
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
                eventsResult = { listOf(original) }
                participateResult = { original.copy(participatedByMe = true, participants = 4) }
            },
            computationDispatcher = testDispatcher,
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
                eventsResult = { listOf(original) }
                participateResult = { throw RuntimeException("participate failed") }
            },
            computationDispatcher = testDispatcher,
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
                eventsResult = { listOf(event(1L)) }
                saveResult = { event(99L, "New") }
            },
            computationDispatcher = testDispatcher,
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
                eventsResult = { listOf(original) }
                saveResult = { original.copy(content = "New content") }
            },
            computationDispatcher = testDispatcher,
        )

        viewModel.accept(EventMessage.AddEvent(id = 1L, text = "New content"))

        assertEquals(1, viewModel.state.events?.size)
        assertEquals("New content", viewModel.state.events?.first()?.content)
    }

    @Test
    fun `saveEvent error - events in state are unchanged`() {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = { listOf(event(1L)) }
                saveResult = { throw RuntimeException("save failed") }
            },
            computationDispatcher = testDispatcher,
        )

        viewModel.accept(EventMessage.AddEvent(id = 0L, text = "content"))

        assertEquals(1, viewModel.state.events?.size)
        assertEquals(1L, viewModel.state.events?.first()?.id)
    }

    @Test
    fun `deleteEvent success - event is removed from list`() {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = { listOf(event(1L), event(2L)) }
                deleteResult = {}
            },
            computationDispatcher = testDispatcher,
        )

        viewModel.accept(EventMessage.Delete(id = 1L))

        assertEquals(listOf(2L), viewModel.state.events?.map { it.id })
    }

    @Test
    fun `deleteEvent error - events in state are unchanged`() {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = { listOf(event(1L), event(2L)) }
                deleteResult = { throw RuntimeException("delete failed") }
            },
            computationDispatcher = testDispatcher,
        )

        viewModel.accept(EventMessage.Delete(id = 1L))

        assertEquals(listOf(1L, 2L), viewModel.state.events?.map { it.id })
    }

    @Test
    fun `share - emits Share effect with content`() = runTest {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = { listOf(event(1L, "Hello")) }
            },
            computationDispatcher = testDispatcher,
        )
        val effects = mutableListOf<EventEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.toList(effects)
        }

        viewModel.accept(EventMessage.Share(id = 1L))

        assertEquals(listOf(EventEffect.Share("Hello")), effects)
    }

    @Test
    fun `editEvent - emits EditEvent effect with event`() = runTest {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = { listOf(event(1L)) }
            },
            computationDispatcher = testDispatcher,
        )
        val effects = mutableListOf<EventEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.toList(effects)
        }

        val edited = EventUiModel(id = 1L, content = "edit me")
        viewModel.accept(EventMessage.EditEvent(event = edited))

        assertEquals(listOf(EventEffect.EditEvent(edited)), effects)
    }

    @Test
    fun `saveEvent new - emits ScrollTo top`() = runTest {
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = { listOf(event(1L)) }
                saveResult = { event(99L, "New") }
            },
            computationDispatcher = testDispatcher,
        )
        val effects = mutableListOf<EventEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.toList(effects)
        }

        viewModel.accept(EventMessage.AddEvent(id = 0L, text = "New"))

        assertEquals(listOf(EventEffect.ScrollTo(0)), effects)
    }

    @Test
    fun `like error - emits Error effect`() = runTest {
        val error = RuntimeException("like failed")
        viewModel = EventListViewModel(
            repository = FakeEventRepository().apply {
                eventsResult = { listOf(event(1L, likedByMe = false)) }
                likeResult = { throw error }
            },
            computationDispatcher = testDispatcher,
        )
        val effects = mutableListOf<EventEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effects.toList(effects)
        }

        viewModel.accept(EventMessage.Like(id = 1L))

        assertEquals(listOf(EventEffect.Error(error)), effects)
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
    var eventsResult: suspend () -> List<Event> = { emptyList() }
    var likeResult: suspend () -> Event = { throw NotImplementedError() }
    var participateResult: suspend () -> Event = { throw NotImplementedError() }
    var saveResult: suspend () -> Event = { throw NotImplementedError() }
    var deleteResult: suspend () -> Unit = { throw NotImplementedError() }

    override suspend fun getEvents(): List<Event> = eventsResult()
    override suspend fun likeById(id: Long, likedByMe: Boolean): Event = likeResult()
    override suspend fun participateById(id: Long, participatedByMe: Boolean): Event = participateResult()
    override suspend fun saveEvent(id: Long, content: String): Event = saveResult()
    override suspend fun deleteById(id: Long) = deleteResult()
}