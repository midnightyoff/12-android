package com.eltex.androidschool.feauture.event

import androidx.compose.runtime.Immutable

@Immutable
data class EventListState(
    val events: List<EventUiState> = emptyList(),
)
