package com.eltex.androidschool.feauture.event.list

import arrow.core.Either
import com.eltex.androidschool.domain.AppException
import com.eltex.androidschool.feauture.event.domain.Event

sealed interface EventMessage {
    data object Retry : EventMessage

    data object LoadInitial : EventMessage
    data object LoadNextPage : EventMessage
    data class LoadInitialResult(
        val result: Either<AppException, List<Event>>,
    ) : EventMessage

    data class LoadNextPageResult(
        val result: Either<AppException, List<Event>>,
    ) : EventMessage

    data class Like(val id: Long, val liked: Boolean) : EventMessage
    data class LikeError(
        val eventId: Long,
        val originalLikedByMe: Boolean,
        val originalLikes: Int,
        val error: AppException,
    ) : EventMessage

    data class LikeSuccess(val event: Event) : EventMessage

    // Participate
    data class Participate(val id: Long, val participated: Boolean) : EventMessage
    data class ParticipateError(
        val eventId: Long,
        val originalParticipatedByMe: Boolean,
        val originalParticipants: Int,
        val error: AppException,
    ) : EventMessage

    data class ParticipateSuccess(val event: Event) : EventMessage

    // Delete
    data class Delete(val id: Long) : EventMessage
    data class DeleteError(val original: EventUiModel, val error: AppException) : EventMessage

    // Save (add / edit)
    data class AddEvent(val id: Long, val text: String) : EventMessage
    data class AddEventResult(val value: Either<AppException, Event>) : EventMessage

    // One-shot UI
    data class Share(val id: Long) : EventMessage
    data class EditEvent(val event: EventUiModel) : EventMessage
}