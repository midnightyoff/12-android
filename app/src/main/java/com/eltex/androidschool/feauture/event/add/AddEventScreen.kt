package com.eltex.androidschool.feauture.event.add

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.eltex.androidschool.R
import com.eltex.androidschool.feauture.event.NEW_EVENT_RESULT
import com.eltex.androidschool.ui.theme.AndroidTheme

@Composable
fun AddEventScreenRoute(
    navController: NavController = rememberNavController(),
    viewModel: AddEventViewModel = viewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.effects.collect { event ->
            when (event) {
                is AddEventEffect.Saved -> {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(NEW_EVENT_RESULT, event.id to event.text)
                    navController.popBackStack()
                }
            }
        }
    }

    AddEventScreen(
        state = viewModel.state,
        addEventHandler = viewModel::accept,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    state: AddEventState,
    addEventHandler: (AddEventMessage) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.new_event_title))
                },
                navigationIcon = {
                    val backPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
                    IconButton(onClick = {
                        backPressedDispatcherOwner?.onBackPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        addEventHandler(AddEventMessage.Save)
                    }) {
                        Icon(Icons.Default.Check, null)
                    }
                }
            )
        }
    ) {
        TextField(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            value = state.text,
            onValueChange = {
                addEventHandler(AddEventMessage.TextChanged(it))
            },
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AddEventScreenPreview() {
    AndroidTheme() {
        AddEventScreen(AddEventState("Test\n\n1\n\n2\n\n3"), {})
    }
}