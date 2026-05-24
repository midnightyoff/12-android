package com.eltex.androidschool.feauture.event.list

import androidx.compose.runtime.Immutable
import com.eltex.androidschool.domain.LoadingState

@Immutable
data class EventListState(
    val events: List<EventUiModel>? = null,
    val groupedEvents: Map<String, List<EventUiModel>> = emptyMap(),
    val status: LoadingState = LoadingState.Idle,
)