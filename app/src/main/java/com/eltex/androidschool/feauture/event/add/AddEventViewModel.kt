package com.eltex.androidschool.feauture.event.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.eltex.androidschool.Navigation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AddEventViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val args = savedStateHandle.toRoute<Navigation.AddEvent>()

    var state by mutableStateOf(AddEventState(text = args.initialText))
        private set

    private val _effects = MutableSharedFlow<AddEventEffect>(extraBufferCapacity = 64)
    val effects = _effects.asSharedFlow()

    fun accept(message: AddEventMessage) {
        when (message) {
            AddEventMessage.Save -> _effects.tryEmit(AddEventEffect.Saved(args.id, state.text))
            is AddEventMessage.TextChanged -> state = state.copy(text = message.value)
        }
    }
}