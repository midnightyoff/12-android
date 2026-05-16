package com.eltex.androidschool.feauture.auth.ui

import com.eltex.androidschool.feauture.auth.domain.LoginError
import com.eltex.androidschool.feauture.auth.domain.PasswordError


data class AuthState(
    val login: String = "",
    val password: String = "",
    val loginError: LoginError? = null,
    val passwordError: PasswordError? = null,
)
