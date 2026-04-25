package com.eltex.androidschool.feauture.event.list

sealed interface EventMessage {
    data class Like(val id: Long) : EventMessage
    data class Participate(val id: Long) : EventMessage
    data class Share(val id: Long) : EventMessage
    data class AddEvent(val id: Long, val text: String) : EventMessage
    data class Delete(val id: Long) : EventMessage
    data class EditEvent(val event: EventUiState) : EventMessage
}