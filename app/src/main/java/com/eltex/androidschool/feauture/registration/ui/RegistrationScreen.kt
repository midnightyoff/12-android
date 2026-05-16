package com.eltex.androidschool.feauture.registration.ui

import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eltex.androidschool.R
import com.eltex.androidschool.ui.theme.AndroidTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.eltex.androidschool.Navigation

@Composable
fun RegistrationRoute(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            if (effect is RegistrationEffect.Success) {
                Toast.makeText(context, "Registration Success", Toast.LENGTH_SHORT).show()
                navController.popBackStack<Navigation.Main>(false)
            }
        }
    }

    RegistrationScreen(
        state = viewModel.state,
        modifier = modifier,
        onEvent = viewModel::accept
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    state: RegistrationState,
    modifier: Modifier = Modifier,
    onEvent: (RegistrationAction) -> Unit = {}
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.registration_title))
                },
                navigationIcon = {
                    val dispatcherOwner = LocalOnBackPressedDispatcherOwner.current
                    IconButton(onClick = {
                        dispatcherOwner?.onBackPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, "Back button")
                    }
                }
            )
        }
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        val combinedPadding = PaddingValues(
            start = paddingValues.calculateStartPadding(layoutDirection) + 32.dp,
            end = paddingValues.calculateEndPadding(layoutDirection) + 32.dp,
            top = paddingValues.calculateTopPadding() + 32.dp,
            bottom = paddingValues.calculateBottomPadding() + 32.dp,
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(combinedPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = modifier
                    .padding(bottom = 16.dp)
                    .size(160.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.login,
                onValueChange = { if (it.length <= 32) onEvent(RegistrationAction.LoginChanged(it)) },
                label = { Text(stringResource(R.string.login_hint)) },
                isError = state.loginError != null,
                supportingText = { Text(state.loginError.toReadableString().orEmpty()) },
                singleLine = true
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.name,
                onValueChange = { if (it.length <= 128) onEvent(RegistrationAction.NameChanged(it)) },
                label = { Text(stringResource(R.string.name_hint)) },
                isError = state.nameError != null,
                supportingText = { Text(state.nameError.toReadableString().orEmpty()) },
                singleLine = true
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = {
                    if (it.length <= 128) onEvent(
                        RegistrationAction.PasswordChanged(
                            it
                        )
                    )
                },
                label = { Text(stringResource(R.string.password_hint)) },
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                isError = state.passwordError != null,
                supportingText = { Text(state.passwordError.toReadableString().orEmpty()) },
                singleLine = true,
                trailingIcon = {
                    val icon = if (isPasswordVisible) {
                        Icons.Filled.VisibilityOff
                    } else {
                        Icons.Filled.Visibility
                    }

                    val description = if (isPasswordVisible) {
                        stringResource(R.string.hide_password_description)
                    } else {
                        stringResource(R.string.show_password_description)
                    }

                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = description
                        )
                    }
                }
            )

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.confirmPassword,
                onValueChange = { onEvent(RegistrationAction.ConfirmPasswordChanged(it)) },
                label = { Text(stringResource(R.string.confirm_password_hint)) },
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                isError = state.confirmPasswordError != null,
                supportingText = { Text(state.confirmPasswordError.toReadableString().orEmpty()) },
                singleLine = true,
                trailingIcon = {
                    val icon = if (isPasswordVisible) {
                        Icons.Filled.VisibilityOff
                    } else {
                        Icons.Filled.Visibility
                    }

                    val description = if (isPasswordVisible) {
                        stringResource(R.string.hide_password_description)
                    } else {
                        stringResource(R.string.show_password_description)
                    }

                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = description
                        )
                    }
                }
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onEvent(RegistrationAction.Submit) }) {
                Text(stringResource(R.string.login))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    AndroidTheme {
        RegistrationScreen(RegistrationState())
    }
}
