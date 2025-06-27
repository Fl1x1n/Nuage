    package pt.nuage.ui.navigation

    sealed class Screen(val route: String) {
        object HomeGraph : Screen("home_graph")
        object Dashboard : Screen("home_graph/dashboard")
        object DailyDetail : Screen("home_graph/daily")
        object About : Screen("about")
    }