package com.eltex.androidschool.feauture.event.list

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eltex.androidschool.data.AppDb
import com.eltex.androidschool.feauture.event.data.EventRepositoryImpl
import com.eltex.androidschool.feauture.event.domain.EventRepository
import com.eltex.androidschool.feauture.event.list.EventEffect.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

class EventListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventRepository = EventRepositoryImpl(
        AppDb.getInstance(application).eventsDao
    )
    var state by mutableStateOf(EventListState())
        private set
    private val _effects = MutableSharedFlow<EventEffect>(extraBufferCapacity = 64)
    val effects = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.events.map {
                it.map(EventUiState::fromEvent)
            }
                .collect {
                    state = state.copy(
                        events = it,
                        groupedEvents = groupByDate(it)
                    )
                }
        }
    }

    fun accept(action: EventMessage) {
        when (action) {
            is EventMessage.Like -> repository.likeById(action.id)
            is EventMessage.Participate -> repository.participateById(action.id)
            is EventMessage.Share -> {
                val event = state.events.find { it.id == action.id }
                event?.let {
                    _effects.tryEmit(Share(it.content))
                }
            }

            is EventMessage.AddEvent -> repository.saveEvent(action.id, action.text)
            is EventMessage.Delete -> repository.deleteById(action.id)
            is EventMessage.EditEvent -> _effects.tryEmit(EditEvent(action.event))
        }
    }

    private fun groupByDate(events: List<EventUiState>): Map<Instant, List<EventUiState>> {
        return events.groupBy {
            it.published.atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        }
    }
}
