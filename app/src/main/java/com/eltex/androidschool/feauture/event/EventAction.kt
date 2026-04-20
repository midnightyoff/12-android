package com.eltex.androidschool.feauture.event

sealed interface EventAction {
    data class Like(val id: Long) : EventAction
    data class Participate(val id: Long) : EventAction
    data class Share(val id: Long) : EventAction
    data class Menu(val id: Long) : EventAction
}