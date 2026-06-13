package com.eltex.androidschool.tea

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class Store<State, Message, Effect>(
    private val reducer: Reducer<State, Message, Effect>,
    private val effectHandler: EffectHandler<Message, Effect>,
    initialState: State,
    private val initialMessages: Set<Message> = emptySet(),
) {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val messages = MutableSharedFlow<Message>()
    private val _effects = MutableSharedFlow<Effect>()
    val effects = _effects.asSharedFlow()

    suspend fun accept(message: Message) {
        messages.emit(message)
    }

    suspend fun connect() = coroutineScope {
        launch {
            effectHandler.connect(effects).collect {
                messages.emit(it)
            }
        }

        launch {
            listOf(
                initialMessages.asFlow(),
                messages,
            )
                .merge()
                .map { message ->
                    reducer.reduce(_state.value, message)
                }
                .collect { result ->
                    _state.value = result.newState
                    result.effects.forEach { effect ->
                        _effects.emit(effect)
                    }
                }
        }
    }
}