package com.eltex.androidschool.feauture.event

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eltex.androidschool.ui.theme.AndroidTheme


@Composable
fun EventListScreenRoute(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
    viewModel: EventListViewModel = viewModel(),
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
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
            }
        }
    }

    EventListScreen(
        state = viewModel.state,
        modifier = modifier,
        contentPadding = contentPadding,
        eventListHandler = viewModel::accept
    )
}

@Composable
fun EventListScreen(
    state: EventListState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues.Zero,
    eventListHandler: (EventAction) -> Unit = {},
) {


    val layoutDirection = LocalLayoutDirection.current
    val combinedPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection) + 8.dp,
        end = contentPadding.calculateEndPadding(layoutDirection) + 8.dp,
        top = contentPadding.calculateTopPadding(),
        bottom = contentPadding.calculateBottomPadding(),
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = combinedPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.events, key = { it.id }) { event ->
            EventCard(
                event = event,
                onEvent = eventListHandler
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun EventListScreenPreview() {
    AndroidTheme {
        EventListScreen(
            EventListState(
                listOf(
                    EventUiState(
                        id = 2L,
                        author = "Lydia Westervelt",
                        published = "11.05.22 11:21",
                        type = EventType.OFFLINE,
                        datetime = "16.05.22 12:00",
                        content = "$2: Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
                        link = "https://m2.material.io/components/cards",
                        likes = 2,
                        participants = 2
                    ),
                    EventUiState(
                        id = 1L,
                        author = "Lydia Westervelt",
                        published = "11.05.22 11:21",
                        type = EventType.OFFLINE,
                        datetime = "16.05.22 12:00",
                        content = "$1: Приглашаю провести уютный вечер за увлекательными играми! У нас есть несколько вариантов настолок, подходящих для любой компании.",
                        link = "https://m2.material.io/components/cards",
                        likes = 2,
                        participants = 2
                    )
                )
            )
        )
    }
}
