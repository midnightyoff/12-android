package com.eltex.androidschool.feauture.event.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.eltex.androidschool.Navigation
import com.eltex.androidschool.R
import com.eltex.androidschool.domain.LoadingState
import com.eltex.androidschool.feauture.event.NEW_EVENT_RESULT
import com.eltex.androidschool.domain.AppException
import com.eltex.androidschool.feauture.event.domain.EventType
import com.eltex.androidschool.ui.ErrorScreen
import com.eltex.androidschool.ui.LoadingScreen
import com.eltex.androidschool.ui.theme.AndroidTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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
                is EventEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        @SuppressLint("LocalContextGetResourceValueCall")
                        context.getString(effect.textResId),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is EventEffect.ScrollTo -> {
                    listState.animateScrollToItem(effect.index)
                }

                is EventEffect.Share -> {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, effect.content)
                        type = "text/plain"
                    }
                    val chooser = Intent.createChooser(intent, null)
                    context.startActivity(chooser)
                }

                is EventEffect.EditEvent -> {
                    navController.navigate(
                        Navigation.AddEvent(
                            id = effect.event.id,
                            initialText = effect.event.content
                        )
                    )
                }

                is EventEffect.Error -> {
                    Toast.makeText(
                        context,
                        @SuppressLint("LocalContextGetResourceValueCall")
                        effect.error.toReadableFormat(context),
                        Toast.LENGTH_SHORT,
                    ).show()
                }
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

    when (viewModel.state.status) {
        is LoadingState.Loading if viewModel.state.events == null ->
            LoadingScreen(modifier = modifier)

        is LoadingState.Error if viewModel.state.events == null ->
            ErrorScreen(
                onRetry = { viewModel.accept(EventMessage.Retry) },
                modifier = modifier,
                text = (viewModel.state.status as LoadingState.Error).value
                    .toReadableFormat(context),
            )

        else -> EventListScreen(
            state = viewModel.state,
            listState = listState,
            modifier = modifier,
            contentPadding = contentPadding,
            eventListHandler = viewModel::accept
        )
    }
}

@Composable
fun EventListScreen(
    state: EventListState,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    eventListHandler: (EventMessage) -> Unit = {},
) {
    val layoutDirection = LocalLayoutDirection.current
    val combinedPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection) + 8.dp,
        end = contentPadding.calculateEndPadding(layoutDirection) + 8.dp,
        top = contentPadding.calculateTopPadding(),
        bottom = contentPadding.calculateBottomPadding(),
    )

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = combinedPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.groupedEvents.forEach { (dateStr, eventsInGroup) ->
            item(
                key = dateStr,
                contentType = "header"
            ) {
                DateHeader(dateStr)
            }

            items(
                items = eventsInGroup,
                key = { it.id },
                contentType = { "event" }
            ) { event ->
                EventCard(
                    event = event,
                    modifier = Modifier.animateItem(),
                    onEvent = eventListHandler,
                )
            }
        }
    }
}

private fun Throwable.toReadableFormat(context: Context): String = when (this) {
    is AppException.Forbidden -> context.getString(R.string.forbidden_error)
    is AppException.NetworkException -> context.getString(R.string.network_error)
    else -> context.getString(R.string.unknown_error)
}

@Composable
fun DateHeader(dateStr: String) {
    val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yy"))
    val text = when (date) {
        LocalDate.now() -> stringResource(R.string.today)
        LocalDate.now().minusDays(1) -> stringResource(R.string.yesterday)
        else -> DateTimeFormatter.ofPattern("dd MMM yyyy").format(date)
    }

    Text(
        text = text,
        modifier = Modifier.padding(16.dp),
        fontSize = 20.sp
    )
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun EventListScreenPreview() {
    AndroidTheme {
        val previewListState = rememberLazyListState()
        EventListScreen(
            EventListState(
                listOf(
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
                    )
                )
            ),
            listState = previewListState
        )
    }
}
