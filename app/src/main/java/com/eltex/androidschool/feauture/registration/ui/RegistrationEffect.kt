package com.eltex.androidschool.feauture.registration.ui

sealed interface RegistrationEffect {
    data object Success : RegistrationEffect
}