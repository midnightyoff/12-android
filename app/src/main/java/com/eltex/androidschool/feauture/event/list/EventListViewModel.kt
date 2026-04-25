package com.eltex.androidschool.feauture.event.list


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eltex.androidschool.feauture.event.list.EventEffect.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.Instant
import java.time.ZoneId

class EventListViewModel : ViewModel() {
    private val initialEvents = List(1, ::createEvent)

    var state by mutableStateOf(
        EventListState(
            events = initialEvents,
            groupedEvents = groupByDate(initialEvents)
        )
    )
        private set
    private val _effects = MutableSharedFlow<EventEffect>(extraBufferCapacity = 64)
    val effects = _effects.asSharedFlow()

    fun accept(action: EventMessage) {
        when (action) {
            is EventMessage.Like -> likeById(action.id)
            is EventMessage.Participate -> participateById(action.id)
            is EventMessage.Share -> {
                val event = state.events.find { it.id == action.id }
                event?.let {
                    _effects.tryEmit(Share(it.content))
                }
            }
            is EventMessage.AddEvent -> saveEvent(action.id, action.text)
            is EventMessage.Delete -> deleteEvent(action.id)
            is EventMessage.EditEvent -> _effects.tryEmit(EditEvent(action.event))
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
    private fun saveEvent(id: Long, text: String) {
        if (id == 0L) {
            addEvent(text)
        } else {
            updateState(state.events.map {
                if (it.id == id) it.copy(content = text) else it
            })
        }
    }

    private fun addEvent(text: String) {
        updateState(
            listOf(
                EventUiState(
                    id = (state.events.maxByOrNull { it.id }?.id ?: 0) + 1,
                    content = text,
                    author = "Me",
                    published = Instant.now()
                )
            ) + state.events
        )
        _effects.tryEmit(ScrollTo(0))
    }

    private fun deleteEvent(id: Long) {
        updateState(state.events.filter { it.id != id })
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