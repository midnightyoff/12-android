package com.eltex.androidschool.feauture.event


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eltex.androidschool.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.Instant
import java.time.ZoneId

class EventListViewModel : ViewModel() {
    private val initialEvents = List(10_000, ::createEvent)

    var state by mutableStateOf(
        EventListState(
            events = initialEvents,
            groupedEvents = groupByDate(initialEvents)
        )
    )
        private set
    private val _effects = MutableSharedFlow<EventEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun accept(action: EventAction) {
        when (action) {
            is EventAction.Like -> likeById(action.id)
            is EventAction.Participate -> participateById(action.id)
            is EventAction.Share -> _effects.tryEmit(EventEffect.ShowToast(R.string.not_implemented))
            is EventAction.Menu -> _effects.tryEmit(EventEffect.ShowToast(R.string.not_implemented))
        }
    }

    private fun likeById(id: Long) {
        updateState(state.events.map {
            if (it.id == id) it.copy(
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1,
                likedByMe = !it.likedByMe
            ) else it
        })
    }

    private fun participateById(id: Long) {
        updateState(state.events.map {
            if (it.id == id) it.copy(
                participants = if (it.participatedByMe) it.participants - 1 else it.participants + 1,
                participatedByMe = !it.participatedByMe
            ) else it
        })
    }

    private fun updateState(newList: List<EventUiState>) {
        state = state.copy(
            events = newList,
            groupedEvents = groupByDate(newList)
        )
    }

    private fun groupByDate(events: List<EventUiState>): Map<Instant, List<EventUiState>> {
        return events.groupBy {
            it.published.atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        }
    }

    private fun createEvent(id: Int) = EventUiState(
        id = id + 1L,
        author = "Lydia Westervelt",
        published = Instant.now().minusSeconds(id / 3 * 24 * 3600L),
        type = EventType.OFFLINE,
        datetime = Instant.now().minusSeconds(id / 3 * 24 * 3600L),
        content = "$id: Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
        link = "https://m2.material.io/components/cards",
        likes = 2,
        participants = 2
    )
}