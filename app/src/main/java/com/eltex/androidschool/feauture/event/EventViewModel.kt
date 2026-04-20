package com.eltex.androidschool.feauture.event


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eltex.androidschool.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class EventViewModel : ViewModel() {
    var state by mutableStateOf(createEvent())
        private set
    private val _effects = MutableSharedFlow<EventEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun accept(action: EventAction) {
        when (action) {
            EventAction.Like -> {
                state = state.copy(
                    likes = if (state.likedByMe) state.likes - 1 else state.likes + 1,
                    likedByMe = !state.likedByMe
                )
            }

            EventAction.Participate -> {
                state = state.copy(
                    participants = if (state.participatedByMe) state.participants - 1 else state.participants + 1,
                    participatedByMe = !state.participatedByMe
                )
            }

            EventAction.Share -> {
                _effects.tryEmit(EventEffect.ShowToast(R.string.not_implemented))
            }

            EventAction.Menu -> {
                _effects.tryEmit(EventEffect.ShowToast(R.string.not_implemented))
            }
        }
    }

    private fun createEvent() = EventUiState(
        id = 1L,
        author = "Lydia Westervelt",
        published = "11.05.22 11:21",
        type = EventType.OFFLINE,
        datetime = "16.05.22 12:00",
        content = "Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
        link = "https://m2.material.io/components/cards",
        likes = 2,
        participants = 2
    )
}