package com.eltex.androidschool

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eltex.androidschool.feauture.auth.ui.AuthScreenRoute
import com.eltex.androidschool.feauture.event.add.AddEventScreenRoute
import com.eltex.androidschool.feauture.main.MainScreen
import com.eltex.androidschool.feauture.registration.ui.RegistrationRoute
import kotlinx.serialization.Serializable

@Composable
fun NavigationScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Navigation.Main) {
        composable<Navigation.Main> {
            MainScreen(navController)
        }

        composable<Navigation.AddEvent> {
            AddEventScreenRoute(navController)
        }

        composable<Navigation.Registration> {
            RegistrationRoute(navController = navController)
        }

        composable<Navigation.Authentication> {
            AuthScreenRoute(navController = navController)
        }
    }
}

@Serializable
sealed interface Navigation {
    @Serializable
    object Main : Navigation

    @Serializable
    data class AddEvent(
        val id: Long = 0L,
        val initialText: String = ""
    ) : Navigation

    @Serializable
    object Registration : Navigation

    @Serializable
    object Authentication : Navigation
}