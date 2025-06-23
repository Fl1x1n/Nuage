package pt.nuage.ui.navigation


sealed class Screens(var route: String) {
    object App : Screens("home") {
        object Home : Screens("dashboard")
        object Daily : Screens("daily")
    }

    object About : Screens("about")
}