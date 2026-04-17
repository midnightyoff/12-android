package com.eltex.androidschool.feauture.registration.ui

sealed interface RegistrationAction {
    data class LoginChanged(val value: String) : RegistrationAction
    data class NameChanged(val value: String) : RegistrationAction
    data class PasswordChanged(val value: String) : RegistrationAction
    data class ConfirmPasswordChanged(val value: String) : RegistrationAction
    data object Submit : RegistrationAction
}