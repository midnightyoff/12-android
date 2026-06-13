package com.eltex.androidschool.feauture.event.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.eltex.androidschool.Navigation
import com.eltex.androidschool.R
import com.eltex.androidschool.domain.AppException
import com.eltex.androidschool.domain.LoadingState
import com.eltex.androidschool.feauture.event.NEW_EVENT_RESULT
import com.eltex.androidschool.feauture.event.domain.EventType
import com.eltex.androidschool.ui.ErrorScreen
import com.eltex.androidschool.ui.LoadingScreen
import com.eltex.androidschool.ui.theme.AndroidTheme

@Composable
fun EventListScreenRoute(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: EventListViewModel = viewModel(),
    navController: NavController = rememberNavController(),
    listState: LazyListState = rememberLazyListState(),
) {
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is EventEffect.ScrollTo -> listState.animateScrollToItem(effect.index)

                is EventEffect.Share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, effect.content)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(intent, null))
                }

                is EventEffect.EditEvent -> {
                    navController.navigate(
                        Navigation.AddEvent(
                            id = effect.event.id,
                            initialText = effect.event.content,
                        )
                    )
                }

                is EventEffect.Error -> {
                    Toast.makeText(
                        context,
                        @SuppressLint("LocalContextGetResourceValueCall")
                        effect.value.toReadableFormat(context),
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                else -> Unit
            }
        }
    }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<Pair<Long, String>?>(NEW_EVENT_RESULT, null)
            ?.collect { result ->
                result?.let { (id, text) ->
                    viewModel.accept(EventMessage.AddEvent(id, text))
                }
                savedStateHandle.remove<Pair<Long, String>?>(NEW_EVENT_RESULT)
            }
    }

    val state by viewModel.state.collectAsState()

    when {
        state.isEmptyError -> ErrorScreen(
            onRetry = { viewModel.accept(EventMessage.Retry) },
            modifier = modifier,
            text = (state.status as LoadingState.Error).value.toReadableFormat(context),
        )

        state.isEmptyLoading -> LoadingScreen(modifier = modifier)

        else -> EventListScreen(
            events = state.events.orEmpty(),
            isRefreshing = state.isRefreshing,
            listState = listState,
            modifier = modifier,
            contentPadding = contentPadding,
            eventListHandler = viewModel::accept,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    events: List<EventUiModel>,
    isRefreshing: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    eventListHandler: (EventMessage) -> Unit = {},
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { eventListHandler(EventMessage.LoadInitial) },
        modifier = modifier,
    ) {
        val layoutDirection = LocalLayoutDirection.current
        val combinedPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(layoutDirection) + 8.dp,
            end = contentPadding.calculateEndPadding(layoutDirection) + 8.dp,
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding(),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = combinedPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(events, key = { _, it -> it.id }) { index, event ->
                if (index >= events.size - PRELOAD_THRESHOLD) {
                    LaunchedEffect(events.size) {
                        eventListHandler(EventMessage.LoadNextPage)
                    }
                }
                EventCard(
                    event = event,
                    modifier = Modifier.animateItem(),
                    onEvent = eventListHandler,
                )
            }
        }
    }
}

private const val PRELOAD_THRESHOLD = 3

private fun Throwable.toReadableFormat(context: Context): String = when (this) {
    is AppException.Forbidden -> context.getString(R.string.forbidden_error)
    is AppException.NetworkException -> context.getString(R.string.network_error)
    else -> context.getString(R.string.unknown_error)
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun EventListScreenPreview() {
    AndroidTheme {
        EventListScreen(
            events = listOf(
                EventUiModel(
                    id = 2L,
                    author = "Lydia Westervelt",
                    published = "23.05.25 12:00",
                    type = EventType.OFFLINE,
                    datetime = "23.05.25 14:00",
                    content = "$2: Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
                    link = "https://m2.material.io/components/cards",
                    likes = 2,
                    participants = 2
                ),
                EventUiModel(
                    id = 1L,
                    author = "Lydia Westervelt",
                    published = "22.05.25 10:00",
                    type = EventType.OFFLINE,
                    datetime = "22.05.25 15:00",
                    content = "$1: Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
                    link = "https://m2.material.io/components/cards",
                    likes = 2,
                    participants = 2
                ),
            ),
        )
    }
}