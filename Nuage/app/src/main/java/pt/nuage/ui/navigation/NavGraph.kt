package pt.nuage.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pt.nuage.ui.screens.AboutScreen
import pt.nuage.ui.screens.DayScreen
import pt.nuage.ui.screens.HomeScreen
import pt.nuage.ui.screens.HomeScreenViewModel

@Composable
fun SetUpNavGraph(
    context: Context,
    navController: NavHostController,
    innerPadding: PaddingValues,
    secondNavGraph: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screens.App.route
    ) {
        composable(route = Screens.App.route) {
            HomeScreenNav(
                context = context,
                padding = innerPadding,
                navController = secondNavGraph
            )
        }
        composable(Screens.About.route) {
            AboutScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun HomeScreenNav(
    context: Context,
    navController: NavHostController,
    nuageViewModel: HomeScreenViewModel = viewModel(),
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        route = Screens.App.route,
        startDestination = Screens.App.Home.route
    ) {
        composable(Screens.App.Home.route) {
            HomeScreen(
                context = context,
                nuageUiState = nuageViewModel.nuageUiState,
                modifier = Modifier.padding(padding),
                navController = navController,
                viewModel = nuageViewModel
            )
        }
        composable(route = Screens.App.Daily.route) {
            DayScreen(
                viewModel = nuageViewModel,
                modifier = Modifier.padding(padding)
            )
        }
    }
}