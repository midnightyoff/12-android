package com.eltex.androidschool.feauture.event

sealed interface EventEffect {
    data class ShowToast(val textResId: Int) : EventEffect
}