package com.eltex.androidschool.feauture.event

sealed interface EventAction {
    data object Like : EventAction
    data object Participate : EventAction
    data object Share : EventAction
    data object Menu : EventAction
}