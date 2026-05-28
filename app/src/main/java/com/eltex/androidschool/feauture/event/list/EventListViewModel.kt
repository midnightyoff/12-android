package com.eltex.androidschool.feauture.event.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eltex.androidschool.domain.LoadingState
import com.eltex.androidschool.domain.SchedulerProvider
import com.eltex.androidschool.feauture.event.data.EventRepositoryImpl
import com.eltex.androidschool.feauture.event.domain.EventRepository
import com.eltex.androidschool.feauture.event.list.EventEffect.EditEvent
import com.eltex.androidschool.feauture.event.list.EventEffect.ScrollTo
import com.eltex.androidschool.feauture.event.list.EventEffect.Share
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class EventListViewModel(
    private val repository: EventRepository = EventRepositoryImpl(),
    private val schedulers: SchedulerProvider = SchedulerProvider
) : ViewModel() {

    private val disposable = CompositeDisposable()

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
        repository.likeById(message.id, likedByMe)
            .map { event ->
                state.events.orEmpty().map {
                    if (it.id == message.id) EventUiModel.fromEvent(event) else it
                }
            }
            .subscribeBy(
                onSuccess = { events ->
                    state = state.copy(events = events, groupedEvents = groupByDate(events))
                },
                onError = { _effects.tryEmit(EventEffect.Error(it as? Exception ?: RuntimeException(it))) },
            )
            .addTo(disposable)
    }

    private fun participate(message: EventMessage.Participate) {
        val participatedByMe = state.events.orEmpty()
            .find { it.id == message.id }?.participatedByMe ?: return
        repository.participateById(message.id, participatedByMe)
            .map { event ->
                state.events.orEmpty().map {
                    if (it.id == message.id) EventUiModel.fromEvent(event) else it
                }
            }
            .subscribeBy(
                onSuccess = { events ->
                    state = state.copy(events = events, groupedEvents = groupByDate(events))
                },
                onError = { _effects.tryEmit(EventEffect.Error(it as? Exception ?: RuntimeException(it))) },
            )
            .addTo(disposable)
    }

    private fun saveEvent(message: EventMessage.AddEvent) {
        repository.saveEvent(message.id, message.text)
            .map { event ->
                val saved = EventUiModel.fromEvent(event)
                if (message.id == 0L) {
                    listOf(saved) + state.events.orEmpty()
                } else {
                    state.events.orEmpty().map { if (it.id == message.id) saved else it }
                }
            }
            .subscribeBy(
                onSuccess = { events ->
                    state = state.copy(events = events, groupedEvents = groupByDate(events))
                },
                onError = { _effects.tryEmit(EventEffect.Error(it as? Exception ?: RuntimeException(it))) },
            )
            .addTo(disposable)
        if (message.id == 0L) {
            _effects.tryEmit(ScrollTo(0))
        }
    }

    private fun deleteEvent(message: EventMessage.Delete) {
        repository.deleteById(message.id)
            .subscribeBy(
                onComplete = {
                    val newEvents = state.events.orEmpty().filter { it.id != message.id }
                    state = state.copy(events = newEvents, groupedEvents = groupByDate(newEvents))
                },
                onError = { _effects.tryEmit(EventEffect.Error(it as? Exception ?: RuntimeException(it))) },
            )
            .addTo(disposable)
    }

    private fun load() {
        state = state.copy(status = LoadingState.Loading)
        repository.getEvents()
            .observeOn(schedulers.computation())
            .map { events -> events.map(EventUiModel::fromEvent) }
            .observeOn(schedulers.mainThread())
            .subscribeBy(
                onSuccess = { events ->
                    state = state.copy(
                        events = events,
                        groupedEvents = groupByDate(events),
                        status = LoadingState.Idle,
                    )
                },
                onError = { state = state.copy(status = LoadingState.Error(it as? Exception ?: RuntimeException(it))) },
            )
            .addTo(disposable)
    }

    public override fun onCleared() {
        disposable.dispose()
    }

    private fun groupByDate(events: List<EventUiModel>): Map<String, List<EventUiModel>> {
        return events.groupBy { it.published.take(8) }
    }
}