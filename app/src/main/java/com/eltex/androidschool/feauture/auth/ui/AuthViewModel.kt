package com.eltex.androidschool.feauture.auth.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eltex.androidschool.feauture.auth.domain.validateLogin
import com.eltex.androidschool.feauture.auth.domain.validatePassword
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AuthViewModel : ViewModel() {
    var state by mutableStateOf(AuthState())
        private set
    private val _effects = MutableSharedFlow<AuthEffect>(
        extraBufferCapacity = 1
    )
    val effects = _effects.asSharedFlow()

    fun accept(message: AuthMessage) {
        when (message) {
            is AuthMessage.LoginChanged -> {
                state = state.copy(login = message.value, loginError = null)
            }

            is AuthMessage.PasswordChanged -> {
                state = state.copy(password = message.value, passwordError = null)
            }

            AuthMessage.Submit -> {
                val loginError = validateLogin(state.login)
                val passwordError = validatePassword(state.password)

                state = state.copy(
                    loginError = loginError,
                    passwordError = passwordError
                )

                if (loginError == null && passwordError == null) {
                    _effects.tryEmit(AuthEffect.ShowSuccess)
                }
            }
        }
    }
}
