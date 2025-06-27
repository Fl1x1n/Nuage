package pt.nuage.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector


data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String // The route must be a String
)