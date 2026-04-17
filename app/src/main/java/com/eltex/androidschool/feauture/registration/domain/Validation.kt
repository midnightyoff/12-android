package com.eltex.androidschool.feauture.registration.domain

sealed interface LoginError
sealed interface NameError
sealed interface PasswordError
sealed interface ConfirmPasswordError {
    object Mismatch : ConfirmPasswordError
}

object Empty : LoginError, NameError, PasswordError
object TooShort : LoginError, PasswordError
object TooLong : LoginError, NameError, PasswordError

fun validateLogin(login: String): LoginError? = when {
    login.isBlank() -> Empty
    login.length < 3 -> TooShort
    login.length > 32 -> TooLong
    else -> null
}

fun validateName(name: String): NameError? = when {
    name.isBlank() -> Empty
    name.length > 128 -> TooLong
    else -> null
}

fun validatePassword(password: String): PasswordError? = when {
    password.isBlank() -> Empty
    password.length <= 8 -> TooShort
    password.length > 128 -> TooLong
    else -> null
}

fun validateConfirmPassword(password: String, confirm: String): ConfirmPasswordError? =
    if (password != confirm) ConfirmPasswordError.Mismatch else null