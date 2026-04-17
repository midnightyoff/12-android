package com.eltex.androidschool.feauture.registration.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.eltex.androidschool.feauture.registration.domain.validateConfirmPassword
import com.eltex.androidschool.feauture.registration.domain.validateLogin
import com.eltex.androidschool.feauture.registration.domain.validateName
import com.eltex.androidschool.feauture.registration.domain.validatePassword
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RegistrationViewModel : ViewModel() {
    var state by mutableStateOf(RegistrationState())
        private set

    private val _effects = MutableSharedFlow<RegistrationEffect>(extraBufferCapacity = 1)
    val effects = _effects.asSharedFlow()

    fun accept(action: RegistrationAction) {
        when (action) {
            is RegistrationAction.LoginChanged ->
                state = state.copy(login = action.value, loginError = null)

            is RegistrationAction.NameChanged ->
                state = state.copy(name = action.value, nameError = null)

            is RegistrationAction.PasswordChanged ->
                state = state.copy(password = action.value, passwordError = null)

            is RegistrationAction.ConfirmPasswordChanged ->
                state = state.copy(confirmPassword = action.value, confirmPasswordError = null)

            RegistrationAction.Submit -> {
                val loginErr = validateLogin(state.login)
                val nameErr = validateName(state.name)
                val passErr = validatePassword(state.password)
                val confirmErr = validateConfirmPassword(state.password, state.confirmPassword)

                state = state.copy(
                    loginError = loginErr,
                    nameError = nameErr,
                    passwordError = passErr,
                    confirmPasswordError = confirmErr
                )

                if (loginErr == null && nameErr == null && passErr == null && confirmErr == null) {
                    _effects.tryEmit(RegistrationEffect.Success)
                }
            }
        }
    }
}