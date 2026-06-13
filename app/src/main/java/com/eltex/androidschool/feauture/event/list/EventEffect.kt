package com.eltex.androidschool.feauture.event.list

sealed interface EventEffect {
    // Data effects (handled by EventListEffectHandler)
    data class LoadInitial(val size: Int) : EventEffect
    data class LoadNextPage(val id: Long, val size: Int) : EventEffect
    data class Like(val liked: Boolean, val data: LikeData) : EventEffect
    data class Participate(val participated: Boolean, val data: ParticipateData) : EventEffect
    data class Save(val id: Long, val content: String) : EventEffect
    data class Delete(val original: EventUiModel) : EventEffect

    // UI effects (handled by the screen)
    data class ScrollTo(val index: Int) : EventEffect
    data class Share(val content: String) : EventEffect
    data class EditEvent(val event: EventUiModel) : EventEffect
    data class Error(val value: Throwable) : EventEffect
}

data class LikeData(
    val id: Long,
    val originalLikedByMe: Boolean,
    val originalLikes: Int,
)

data class ParticipateData(
    val id: Long,
    val originalParticipatedByMe: Boolean,
    val originalParticipants: Int,
)