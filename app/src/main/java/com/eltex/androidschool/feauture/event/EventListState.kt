package com.eltex.androidschool.feauture.event

import androidx.compose.runtime.Immutable
import java.time.Instant

@Immutable
data class EventListState(
    val events: List<EventUiState> = emptyList(),
    val groupedEvents: Map<Instant, List<EventUiState>> = emptyMap()
)
