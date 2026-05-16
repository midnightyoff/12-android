package com.eltex.androidschool.feauture.event.list

sealed interface EventEffect {
    data class ShowToast(val textResId: Int) : EventEffect
    data class ScrollTo(val index: Int) : EventEffect
    data class Share(val content: String) : EventEffect

    data class EditEvent(val event: EventUiState) : EventEffect
}