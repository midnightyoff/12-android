package com.eltex.androidschool.tea

import kotlinx.coroutines.flow.Flow

fun interface EffectHandler<Message, Effect> {
    fun connect(effects: Flow<Effect>): Flow<Message>
}