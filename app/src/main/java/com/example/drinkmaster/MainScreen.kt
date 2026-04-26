package com.example.drinkmaster

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import com.example.drinkmaster.screens.DrinkDetailScreen
import com.example.drinkmaster.screens.HomeScreen
import com.example.drinkmaster.screens.MapScreen
import com.example.drinkmaster.screens.MyMenuScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentUserEmail: String,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Ukrywamy bottom bar na ekranie szczegółów
    val showBottomBar = currentDestination?.route?.startsWith("detail/") == false

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "DrinkMaster",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    label = {
                        Text(currentUserEmail.ifBlank { "Zalogowany uzytkownik" })
                    },
                    selected = false,
                    onClick = { }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    label = { Text("Projekt DrinkMaster") },
                    selected = false,
                    onClick = { }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    label = { Text("Wyloguj") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("DrinkMaster") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        bottomNavigationDestinations.forEach { destination ->
                            val selected = currentDestination?.hierarchy?.any {
                                it.route == destination.route
                            } == true
                            NavigationBarItem(
                                icon = { Icon(destination.icon, contentDescription = destination.label) },
                                label = { Text(destination.label) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = DrinkMasterDestination.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(DrinkMasterDestination.Home.route) {
                    HomeScreen(
                        onDrinkClick = { drinkId ->
                            navController.navigate("detail/$drinkId")
                        }
                    )
                }
                composable(DrinkMasterDestination.Map.route) {
                    MapScreen()
                }
                composable(DrinkMasterDestination.MyMenu.route) {
                    MyMenuScreen(
                        onDrinkClick = { drinkId ->
                            navController.navigate("detail/$drinkId")
                        }
                    )
                }
                composable("detail/{drinkId}") { backStackEntry ->
                    val drinkId = backStackEntry.arguments?.getString("drinkId") ?: return@composable
                    DrinkDetailScreen(
                        drinkId = drinkId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}