package com.eltex.androidschool.tea

fun interface Reducer<State, Message, Effect> {
    fun reduce(currentState: State, message: Message): ReducerResult<State, Effect>
}

data class ReducerResult<State, Effect>(
    val newState: State,
    val effects: Set<Effect>,
) {
    constructor(newState: State, effect: Effect? = null) : this(newState, setOfNotNull(effect))
}