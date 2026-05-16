package com.eltex.androidschool.feauture.auth.ui

sealed interface AuthEffect {
    data object ShowSuccess : AuthEffect
}
