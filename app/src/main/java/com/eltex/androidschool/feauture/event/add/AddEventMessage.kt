package com.eltex.androidschool.feauture.event.add

interface AddEventMessage {
    data object Save : AddEventMessage
    data class TextChanged(val value: String) : AddEventMessage
}