package com.aca.people.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aca.people.R
import com.aca.people.presentation.detail.DetailView
import com.aca.people.presentation.favorites.FavoritesScreen
import com.aca.people.presentation.favorites.FavoritesViewModel
import com.aca.people.presentation.home.HomeViewModel
import com.aca.people.presentation.home.UserListView

@Composable
fun AppNavigation(
    onExitApp: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute == AppScreen.HomeScreen.route ||
        currentRoute == AppScreen.FavoritesScreen.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                PeopleBottomNavigationBar(
                    currentRoute = currentRoute,
                    navController = navController
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.HomeScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            appGraph(
                navController = navController,
                onExitApp = onExitApp
            )
        }
    }
}

@Composable
private fun PeopleBottomNavigationBar(
    currentRoute: String?,
    navController: NavHostController
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == AppScreen.HomeScreen.route,
            onClick = { navigateToTopLevel(navController, AppScreen.HomeScreen.route) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = stringResource(id = R.string.nav_people)
                )
            },
            label = { Text(text = stringResource(id = R.string.nav_people)) }
        )
        NavigationBarItem(
            selected = currentRoute == AppScreen.FavoritesScreen.route,
            onClick = { navigateToTopLevel(navController, AppScreen.FavoritesScreen.route) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = stringResource(id = R.string.nav_favorites)
                )
            },
            label = { Text(text = stringResource(id = R.string.nav_favorites)) }
        )
    }
}

private fun navigateToTopLevel(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavGraphBuilder.appGraph(
    navController: NavHostController,
    onExitApp: () -> Unit
) {
    composable(route = AppScreen.HomeScreen.route) {
        val homeViewModel: HomeViewModel = hiltViewModel()
        UserListView(
            viewModel = homeViewModel,
            onExitApp = onExitApp,
            onNavigateToDetails = { uuid ->
                navController.navigate(AppScreen.DetailsScreen.createRoute(uuid))
            }
        )
    }

    composable(route = AppScreen.FavoritesScreen.route) {
        val favoritesViewModel: FavoritesViewModel = hiltViewModel()
        FavoritesScreen(
            viewModel = favoritesViewModel,
            onNavigateToDetails = { uuid ->
                navController.navigate(AppScreen.DetailsScreen.createRoute(uuid))
            }
        )
    }

    composable(
        route = AppScreen.DetailsScreen.route,
        arguments = listOf(
            navArgument(ConstantAppScreenName.UUID_ARG) { type = NavType.StringType }
        )
    ) {
        DetailView(
            onNavigateUp = navController::navigateUp
        )
    }
}
