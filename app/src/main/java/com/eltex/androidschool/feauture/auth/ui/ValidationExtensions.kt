package com.eltex.androidschool.feauture.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eltex.androidschool.R
import com.eltex.androidschool.feauture.auth.domain.Empty
import com.eltex.androidschool.feauture.auth.domain.LoginError
import com.eltex.androidschool.feauture.auth.domain.PasswordError

@Composable
fun LoginError?.toReadableString(): String? = when (this) {
    Empty -> stringResource(R.string.login_empty_error)
    null -> null
}

@Composable
fun PasswordError?.toReadableString(): String? = when (this) {
    Empty -> stringResource(R.string.password_empty_error)
    PasswordError.TooShort -> stringResource(R.string.password_too_short_error)
    null -> null
}