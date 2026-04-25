package com.eltex.androidschool.feauture.auth.ui

sealed interface AuthMessage {
    data class LoginChanged(val value: String) : AuthMessage
    data class PasswordChanged(val value: String) : AuthMessage
    data object Submit : AuthMessage
}
