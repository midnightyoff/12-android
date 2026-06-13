package com.eltex.androidschool.feauture.event.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eltex.androidschool.tea.Store
import kotlinx.coroutines.launch

class EventListViewModel(
    reducer: EventListReducer = EventListReducer(),
    effectHandler: EventListEffectHandler = EventListEffectHandler(),
) : ViewModel() {

    private val store = Store(
        reducer = reducer,
        effectHandler = effectHandler,
        initialState = EventListState(),
        initialMessages = setOf(EventMessage.LoadInitial),
    )

    val state = store.state
    val effects = store.effects

    init {
        viewModelScope.launch {
            store.connect()
        }
    }

    fun accept(message: EventMessage) {
        viewModelScope.launch {
            store.accept(message)
        }
    }
}