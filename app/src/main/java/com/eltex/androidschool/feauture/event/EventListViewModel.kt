package com.eltex.androidschool.feauture.event


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eltex.androidschool.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class EventListViewModel : ViewModel() {
    var state by mutableStateOf(
        EventListState(
            List(10_000, ::createEvent)
        )
    )
        private set
    private val _effects = MutableSharedFlow<EventEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun accept(action: EventAction) {
        when (action) {
            is EventAction.Like -> {
                likeById(action.id)
            }

            is EventAction.Participate -> {
                participateById(action.id)
            }

            is EventAction.Share -> {
                _effects.tryEmit(EventEffect.ShowToast(R.string.not_implemented))
            }

            is EventAction.Menu -> {
                _effects.tryEmit(EventEffect.ShowToast(R.string.not_implemented))
            }
        }
    }

    private fun likeById(id: Long) {
        state = state.copy(
            events = state.events.map { event ->
                if (event.id == id) {
                    event.copy(
                        likes = if (event.likedByMe) event.likes - 1 else event.likes + 1,
                        likedByMe = !event.likedByMe
                    )
                } else event
            },
        )
    }

    private fun participateById(id: Long) {
        state = state.copy(
            events = state.events.map { event ->
                if (event.id == id) {
                    event.copy(
                        participants = if (event.participatedByMe) event.participants - 1 else event.participants + 1,
                        participatedByMe = !event.participatedByMe
                    )
                } else event
            },
        )
    }

    private fun createEvent(id: Int) = EventUiState(
        id = id + 1L,
        author = "Lydia Westervelt",
        published = "11.05.22 11:21",
        type = EventType.OFFLINE,
        datetime = "16.05.22 12:00",
        content = "$id: Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
        link = "https://m2.material.io/components/cards",
        likes = 2,
        participants = 2
    )
}