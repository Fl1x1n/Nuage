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
import androidx.navigation.compose.navigation
import pt.nuage.ui.screens.AboutScreen
import pt.nuage.ui.screens.DayScreen
import pt.nuage.ui.screens.HomeScreen
import pt.nuage.ui.screens.HomeScreenViewModel

@Composable
fun SetUpNavGraph(
    context: Context,
    navController: NavHostController,
    innerPadding: PaddingValues,
    homeScreenViewModel: HomeScreenViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeGraph.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        navigation(
            startDestination = Screen.Dashboard.route,
            route = Screen.HomeGraph.route
        ) {
            composable(route = Screen.Dashboard.route) {
                HomeScreen(
                    context = context,
                    nuageUiState = homeScreenViewModel.nuageUiState,
                    viewModel = homeScreenViewModel,
                    modifier = Modifier,
                    onDayClicked = { dateString ->
                        homeScreenViewModel.executeDailyScreenMapping(dateString)
                        navController.navigate(Screen.DailyDetail.route)
                    },
                )
            }
            composable(route = Screen.DailyDetail.route) {
                DayScreen(
                    viewModel = homeScreenViewModel,
                    modifier = Modifier
                )
            }
        }
        composable(route = Screen.About.route) {
            AboutScreen(modifier = Modifier)
        }
    }
}