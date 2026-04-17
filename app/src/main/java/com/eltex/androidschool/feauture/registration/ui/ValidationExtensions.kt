package com.eltex.androidschool.feauture.registration.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eltex.androidschool.R
import com.eltex.androidschool.feauture.registration.domain.ConfirmPasswordError
import com.eltex.androidschool.feauture.registration.domain.Empty
import com.eltex.androidschool.feauture.registration.domain.LoginError
import com.eltex.androidschool.feauture.registration.domain.NameError
import com.eltex.androidschool.feauture.registration.domain.PasswordError
import com.eltex.androidschool.feauture.registration.domain.TooLong
import com.eltex.androidschool.feauture.registration.domain.TooShort

@Composable
fun LoginError?.toReadableString(): String? = when (this) {
    Empty -> stringResource(R.string.login_empty_error)
    TooShort -> stringResource(R.string.login_too_short_error)
    TooLong -> stringResource(R.string.login_too_long_error)
    null -> null
}

@Composable
fun NameError?.toReadableString(): String? = when (this) {
    Empty -> stringResource(R.string.name_empty_error)
    TooLong -> stringResource(R.string.name_too_long_error)
    null -> null
}

@Composable
fun PasswordError?.toReadableString(): String? = when (this) {
    Empty -> stringResource(R.string.password_empty_error)
    TooShort -> stringResource(R.string.password_too_short_error)
    TooLong -> stringResource(R.string.password_too_long_error)
    null -> null
}

@Composable
fun ConfirmPasswordError?.toReadableString(): String? = when (this) {
    ConfirmPasswordError.Mismatch -> stringResource(R.string.error_mismatch)
    null -> null
}