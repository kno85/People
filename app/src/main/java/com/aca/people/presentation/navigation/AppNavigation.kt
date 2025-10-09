package com.aca.people.presentation.navigation // Asegúrate que el package es correcto

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aca.people.domain.User
import com.aca.people.presentation.detail.DetailView
import com.aca.people.presentation.home.HomeViewModel
import com.aca.people.presentation.home.UserListView

@Composable
fun AppNavigation(
    onExitApp: () -> Unit // 1. Recibe la acción de cierre
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.HomeScreen.route
    ) {
        appGraph(
            navController = navController,
            onExitApp = onExitApp // 2. Pasa la acción al constructor del grafo
        )
    }
}

private fun NavGraphBuilder.appGraph(
    navController: NavHostController,
    onExitApp: () -> Unit // 3. El grafo también la recibe
) {
    composable(route = AppScreen.HomeScreen.route) {
        val homeViewModel: HomeViewModel = hiltViewModel()
        UserListView(
            viewModel = homeViewModel,
            onExitApp = onExitApp, // 4. Se la pasamos a la pantalla de la lista
            onNavigateToDetails = { user ->
                navController.currentBackStackEntry?.savedStateHandle?.set("user", user)
                navController.navigate(AppScreen.DetailsScreen.route)
            }
        )
    }

    // La pantalla de detalle no necesita la acción de cierre, así que no se la pasamos.
    composable(route = AppScreen.DetailsScreen.route) {
        val user = navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")
        DetailView(
            user = user,
            onNavigateUp = navController::navigateUp
        )
    }
}

