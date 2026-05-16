package com.eltex.androidschool.feauture.event.add

sealed interface AddEventEffect {
    data class Saved(val id: Long, val text: String) : AddEventEffect
}