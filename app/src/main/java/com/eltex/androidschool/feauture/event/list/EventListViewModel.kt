package com.eltex.androidschool.feauture.event.list

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.eltex.androidschool.domain.LoadingState
import com.eltex.androidschool.feauture.event.data.EventRepositoryImpl
import com.eltex.androidschool.feauture.event.domain.Callback
import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository
import com.eltex.androidschool.feauture.event.list.EventEffect.EditEvent
import com.eltex.androidschool.feauture.event.list.EventEffect.ScrollTo
import com.eltex.androidschool.feauture.event.list.EventEffect.Share
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class EventListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventRepository = EventRepositoryImpl()

    var state by mutableStateOf(EventListState())
        private set

    private val _effects = MutableSharedFlow<EventEffect>(extraBufferCapacity = 64)
    val effects = _effects.asSharedFlow()

    init {
        load()
    }

    fun accept(message: EventMessage) {
        when (message) {
            is EventMessage.Like -> like(message)
            is EventMessage.Participate -> participate(message)
            is EventMessage.Share -> {
                state.events?.find { it.id == message.id }
                    ?.let { _effects.tryEmit(Share(it.content)) }
            }

            is EventMessage.AddEvent -> saveEvent(message)
            is EventMessage.Delete -> deleteEvent(message)
            is EventMessage.EditEvent -> _effects.tryEmit(EditEvent(message.event))
            EventMessage.Retry -> load()
        }
    }

    private fun like(message: EventMessage.Like) {
        val likedByMe = state.events.orEmpty()
            .find { it.id == message.id }?.likedByMe ?: return
        repository.likeById(message.id, likedByMe, object : Callback<Event> {
            override fun onSuccess(value: Event) {
                val newEvents = state.events.orEmpty().map {
                    if (it.id == message.id) EventUiModel.fromEvent(value) else it
                }
                state = state.copy(events = newEvents, groupedEvents = groupByDate(newEvents))
            }

            override fun onError(error: Exception) {
                _effects.tryEmit(EventEffect.Error(error))
            }
        })
    }

    private fun participate(message: EventMessage.Participate) {
        val participatedByMe = state.events.orEmpty()
            .find { it.id == message.id }?.participatedByMe ?: return
        repository.participateById(message.id, participatedByMe, object : Callback<Event> {
            override fun onSuccess(value: Event) {
                val newEvents = state.events.orEmpty().map {
                    if (it.id == message.id) EventUiModel.fromEvent(value) else it
                }
                state = state.copy(events = newEvents, groupedEvents = groupByDate(newEvents))
            }

            override fun onError(error: Exception) {
                _effects.tryEmit(EventEffect.Error(error))
            }
        })
    }

    private fun saveEvent(message: EventMessage.AddEvent) {
        repository.saveEvent(message.id, message.text, object : Callback<Event> {
            override fun onSuccess(value: Event) {
                val saved = EventUiModel.fromEvent(value)
                val newEvents = if (message.id == 0L) {
                    listOf(saved) + state.events.orEmpty()
                } else {
                    state.events.orEmpty().map { if (it.id == message.id) saved else it }
                }
                state = state.copy(events = newEvents, groupedEvents = groupByDate(newEvents))
            }

            override fun onError(error: Exception) {
                _effects.tryEmit(EventEffect.Error(error))
            }
        })
        if (message.id == 0L) {
            _effects.tryEmit(ScrollTo(0))
        }
    }

    private fun deleteEvent(message: EventMessage.Delete) {
        repository.deleteById(message.id, object : Callback<Unit> {
            override fun onSuccess(value: Unit) {
                val newEvents = state.events.orEmpty().filter { it.id != message.id }
                state = state.copy(events = newEvents, groupedEvents = groupByDate(newEvents))
            }

            override fun onError(error: Exception) {
                _effects.tryEmit(EventEffect.Error(error))
            }
        })
    }

    private fun load() {
        state = state.copy(status = LoadingState.Loading)

        repository.getEvents(object : Callback<List<Event>> {
            override fun onSuccess(value: List<Event>) {
                val uiModels = value.map(EventUiModel::fromEvent)
                state = state.copy(
                    events = uiModels,
                    groupedEvents = groupByDate(uiModels),
                    status = LoadingState.Idle,
                )
            }

            override fun onError(error: Exception) {
                state = state.copy(status = LoadingState.Error(error))
            }
        })
    }

    private fun groupByDate(events: List<EventUiModel>): Map<String, List<EventUiModel>> {
        return events.groupBy { it.published.take(8) }
    }
}