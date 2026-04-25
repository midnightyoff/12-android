package com.eltex.androidschool.feauture.main

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.eltex.androidschool.Navigation
import com.eltex.androidschool.R
import com.eltex.androidschool.feauture.event.list.EventListScreenRoute
import com.eltex.androidschool.ui.theme.AndroidTheme

enum class Tab(
    @param:StringRes val titleRes: Int,
    val icon: ImageVector
) {
    Posts(R.string.tab_posts, Icons.Default.RssFeed),
    Events(R.string.tab_events, Icons.Default.Event),
    Users(R.string.tab_users, Icons.Default.People)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController = rememberNavController(),
) {
    var selectedTab by rememberSaveable { mutableStateOf(Tab.Events) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Navigation.Authentication)
                    }) {
                        Icon(Icons.Default.AccountCircle, null)
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                tab.icon,
                                contentDescription = stringResource(tab.titleRes)
                            )
                        },
                        label = { Text(stringResource(tab.titleRes)) }
                    )
                }
            }
        },
        floatingActionButton = {
            when (selectedTab) {
                Tab.Posts,
                Tab.Events -> {
                    FloatingActionButton(onClick = {
                        navController.navigate(Navigation.AddEvent(id = 0L, initialText = ""))
                    }) {
                        Icon(Icons.Default.Add, null)
                    }
                }
                Tab.Users -> Unit
            }
        }
    ) { insets ->
        val eventListState = rememberLazyListState()
        Crossfade(modifier = Modifier.padding(insets), targetState = selectedTab) { tab ->
            when (tab) {
                Tab.Posts -> Unit
                Tab.Events -> EventListScreenRoute(
                    listState = eventListState,
                    navController = navController,
                )
                Tab.Users -> Unit
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    AndroidTheme {
        MainScreen()
    }
}