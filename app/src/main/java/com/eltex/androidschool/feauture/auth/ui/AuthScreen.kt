package com.eltex.androidschool.feauture.auth.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.eltex.androidschool.Navigation
import com.eltex.androidschool.R
import com.eltex.androidschool.feauture.auth.domain.Empty
import com.eltex.androidschool.ui.theme.AndroidTheme


@Composable
fun AuthScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
    navController: NavController,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AuthEffect.ShowSuccess -> {
                    Toast.makeText(
                        context,
                        @SuppressLint("LocalContextGetResourceValueCall")
                        context.getString(R.string.login_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                }
            }
        }
    }

    AuthScreen(
        viewModel.state,
        modifier,
        viewModel::accept,
        onRegisterClick = { navController.navigate(Navigation.Registration) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    state: AuthState,
    modifier: Modifier = Modifier,
    onEvent: (AuthMessage) -> Unit = {},
    onRegisterClick: () -> Unit,
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.auth_title))
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
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.login,
                onValueChange = {
                    onEvent(AuthMessage.LoginChanged(it))
                },
                isError = state.loginError != null,
                singleLine = true,
                label = {
                    Text(stringResource(R.string.login_hint))
                },
                supportingText = {
                    Text(state.loginError.toReadableString().orEmpty())
                }
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = {
                    onEvent(AuthMessage.PasswordChanged(it))
                },
                isError = state.passwordError != null,
                singleLine = true,
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                label = {
                    Text(stringResource(R.string.password_hint))
                },
                supportingText = {
                    Text(state.passwordError.toReadableString().orEmpty())
                },
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
                onClick = {
                    onEvent(AuthMessage.Submit)
                },
            ) {
                Text(stringResource(R.string.login))
            }
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onRegisterClick
            ) {
                Text("Don’t have an account? Register")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenEmptyPreview() {
    AndroidTheme() {
        AuthScreen(
            AuthState(),
            onRegisterClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenEmptyErrorPreview() {
    AndroidTheme() {
        AuthScreen(
            AuthState(loginError = Empty, passwordError = Empty),
            onRegisterClick = {}
        )
    }
}
