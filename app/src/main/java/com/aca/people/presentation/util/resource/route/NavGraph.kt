package com.aca.people.presentation.util.resource.route

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aca.people.presentation.detail.DetailView
import com.aca.people.presentation.home.HomeScreen
import com.aca.people.presentation.main.MainViewModel

@Composable
fun NavGraph(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.HomeScreen.route,
    ) {

        composable(route = AppScreen.HomeScreen.route) {
            HomeScreen(
                mainViewModel = mainViewModel,
                navController = navController
            )
        }

        composable(route = AppScreen.DetailsScreen.route) {
                DetailView(navController)
        }
    }
}
