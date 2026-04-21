package com.example.drinkmaster

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DrinkMasterDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : DrinkMasterDestination(
        route = "home",
        label = "Home",
        icon = Icons.Filled.Home
    )

    data object Map : DrinkMasterDestination(
        route = "map",
        label = "Mapa",
        icon = Icons.Filled.LocationOn
    )

    data object MyMenu : DrinkMasterDestination(
        route = "my_menu",
        label = "Moje Menu",
        icon = Icons.Filled.Favorite
    )
}

val bottomNavigationDestinations = listOf(
    DrinkMasterDestination.Home,
    DrinkMasterDestination.Map,
    DrinkMasterDestination.MyMenu
)