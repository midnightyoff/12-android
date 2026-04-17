package com.eltex.androidschool.feauture.registration.ui

import com.eltex.androidschool.feauture.registration.domain.ConfirmPasswordError
import com.eltex.androidschool.feauture.registration.domain.LoginError
import com.eltex.androidschool.feauture.registration.domain.NameError
import com.eltex.androidschool.feauture.registration.domain.PasswordError

data class RegistrationState(
    val login: String = "",
    val name: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val loginError: LoginError? = null,
    val nameError: NameError? = null,
    val passwordError: PasswordError? = null,
    val confirmPasswordError: ConfirmPasswordError? = null,
)