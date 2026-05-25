package com.closetmixer.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.closetmixer.android.ui.screen.CalendarScreen
import com.closetmixer.android.ui.screen.OutfitScreen
import com.closetmixer.android.ui.screen.SettingsScreen
import com.closetmixer.android.ui.screen.StatsScreen
import com.closetmixer.android.ui.screen.VoyageScreen
import com.closetmixer.android.ui.screen.WardrobeScreen

sealed class Screen(val route: String, val label: String) {
    object Wardrobe : Screen("wardrobe", "Garde-robe")
    object Outfit : Screen("outfit", "Tenues")
    object Calendar : Screen("calendar", "Calendrier")
    object Voyage : Screen("voyage", "Voyage")
    object Stats : Screen("stats", "Stats")
    object Settings : Screen("settings", "Paramètres")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomItems = listOf(Screen.Wardrobe, Screen.Outfit, Screen.Calendar, Screen.Voyage, Screen.Stats)

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    Screen.Wardrobe -> Icons.Default.Inventory2
                                    Screen.Outfit -> Icons.Default.Checkroom
                                    Screen.Calendar -> Icons.Default.CalendarMonth
                                    Screen.Voyage -> Icons.Default.FlightTakeoff
                                    else -> Icons.Default.Settings
                                },
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Wardrobe.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Wardrobe.route) { WardrobeScreen() }
            composable(Screen.Outfit.route) { OutfitScreen() }
            composable(Screen.Calendar.route) { CalendarScreen() }
            composable(Screen.Voyage.route) { VoyageScreen() }
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
