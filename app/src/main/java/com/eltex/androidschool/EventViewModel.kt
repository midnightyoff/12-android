package com.eltex.androidschool

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class EventViewModel : ViewModel() {
    private val _event = mutableStateOf(createEvent())
    val event: State<EventUiModel>
        get() = _event

    fun like() {
        val currentEvent = _event.value
        _event.value = currentEvent.copy(
            likes = if (currentEvent.likedByMe) {
                currentEvent.likes - 1
            } else currentEvent.likes + 1,
            likedByMe = !currentEvent.likedByMe
        )
    }

    fun participate() {
        val currentEvent = _event.value
        _event.value = currentEvent.copy(
            participants = if (currentEvent.participatedByMe) {
                currentEvent.participants - 1
            } else currentEvent.participants + 1,
            participatedByMe = !currentEvent.participatedByMe
        )
    }

    private fun createEvent() = EventUiModel(
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