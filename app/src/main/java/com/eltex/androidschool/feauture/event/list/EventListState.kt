package com.eltex.androidschool.feauture.event.list

import androidx.compose.runtime.Immutable
import com.eltex.androidschool.domain.LoadingState

@Immutable
data class EventListState(
    val events: List<EventUiModel>? = null,
    val status: LoadingState = LoadingState.Idle,
) {
    val isEmptyLoading: Boolean get() = events == null && status == LoadingState.Loading
    val isEmptyError: Boolean get() = events == null && status is LoadingState.Error
    val isRefreshing: Boolean get() = events != null && status == LoadingState.Loading
}