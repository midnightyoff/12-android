package com.eltex.androidschool.feauture.event.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eltex.androidschool.domain.LoadingState
import com.eltex.androidschool.feauture.event.data.EventRepositoryImpl
import com.eltex.androidschool.feauture.event.domain.EventRepository
import com.eltex.androidschool.feauture.event.list.EventEffect.EditEvent
import com.eltex.androidschool.feauture.event.list.EventEffect.ScrollTo
import com.eltex.androidschool.feauture.event.list.EventEffect.Share
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventListViewModel(
    private val repository: EventRepository = EventRepositoryImpl(),
    private val computationDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    var state by mutableStateOf(EventListState())
        private set

    private val _effects = MutableSharedFlow<EventEffect>(extraBufferCapacity = 64)
    val effects = _effects.asSharedFlow()

    init {
        load()
    }

    fun accept(message: EventMessage) {
        when (message) {
            is EventMessage.Like -> viewModelScope.launch { like(message) }
            is EventMessage.Participate -> viewModelScope.launch { participate(message) }
            is EventMessage.Share -> {
                state.events?.find { it.id == message.id }
                    ?.let { _effects.tryEmit(Share(it.content)) }
            }
            is EventMessage.AddEvent -> viewModelScope.launch { saveEvent(message) }
            is EventMessage.Delete -> viewModelScope.launch { deleteEvent(message) }
            is EventMessage.EditEvent -> _effects.tryEmit(EditEvent(message.event))
            EventMessage.Retry -> load()
        }
    }

    private suspend fun like(message: EventMessage.Like) {
        val likedByMe = state.events.orEmpty()
            .find { it.id == message.id }?.likedByMe ?: return
        try {
            val event = repository.likeById(message.id, likedByMe)
            val newEvents = state.events.orEmpty().map {
                if (it.id == message.id) EventUiModel.fromEvent(event) else it
            }
            state = state.copy(events = newEvents, groupedEvents = groupByDate(newEvents))
        } catch (e: Exception) {
            _effects.tryEmit(EventEffect.Error(e))
        }
    }

    private suspend fun participate(message: EventMessage.Participate) {
        val participatedByMe = state.events.orEmpty()
            .find { it.id == message.id }?.participatedByMe ?: return
        try {
            val event = repository.participateById(message.id, participatedByMe)
            val newEvents = state.events.orEmpty().map {
                if (it.id == message.id) EventUiModel.fromEvent(event) else it
            }
            state = state.copy(events = newEvents, groupedEvents = groupByDate(newEvents))
        } catch (e: Exception) {
            _effects.tryEmit(EventEffect.Error(e))
        }
    }

    private suspend fun saveEvent(message: EventMessage.AddEvent) {
        if (message.id == 0L) _effects.tryEmit(ScrollTo(0))
        try {
            val event = repository.saveEvent(message.id, message.text)
            val saved = EventUiModel.fromEvent(event)
            val newEvents = if (message.id == 0L) {
                listOf(saved) + state.events.orEmpty()
            } else {
                state.events.orEmpty().map { if (it.id == message.id) saved else it }
            }
            state = state.copy(events = newEvents, groupedEvents = groupByDate(newEvents))
        } catch (e: Exception) {
            _effects.tryEmit(EventEffect.Error(e))
        }
    }

    private suspend fun deleteEvent(message: EventMessage.Delete) {
        try {
            repository.deleteById(message.id)
            val newEvents = state.events.orEmpty().filter { it.id != message.id }
            state = state.copy(events = newEvents, groupedEvents = groupByDate(newEvents))
        } catch (e: Exception) {
            _effects.tryEmit(EventEffect.Error(e))
        }
    }

    private fun load() {
        viewModelScope.launch {
            state = state.copy(status = LoadingState.Loading)
            try {
                val events = withContext(computationDispatcher) {
                    repository.getEvents().map(EventUiModel::fromEvent)
                }
                state = state.copy(
                    events = events,
                    groupedEvents = groupByDate(events),
                    status = LoadingState.Idle,
                )
            } catch (e: Exception) {
                state = state.copy(status = LoadingState.Error(e))
            }
        }
    }

    private fun groupByDate(events: List<EventUiModel>): Map<String, List<EventUiModel>> {
        return events.groupBy { it.published.take(8) }
    }
}