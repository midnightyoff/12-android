package com.eltex.androidschool.feauture.event.domain

interface Callback<T> {
    fun onSuccess(value: T)
    fun onError(error: Exception)
}