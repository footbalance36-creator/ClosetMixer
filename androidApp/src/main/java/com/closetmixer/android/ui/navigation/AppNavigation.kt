package com.closetmixer.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FlightTakeoff
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.closetmixer.android.ui.screen.AddArticleScreen
import com.closetmixer.android.ui.screen.CalendarScreen
import com.closetmixer.android.ui.screen.OutfitScreen
import com.closetmixer.android.ui.screen.SettingsScreen
import com.closetmixer.android.ui.screen.StatsScreen
import com.closetmixer.android.ui.screen.VoyageScreen
import com.closetmixer.android.ui.screen.WardrobeScreen

sealed class Screen(val route: String, val label: String) {
    object Wardrobe  : Screen("wardrobe",    "Garde-robe")
    object Outfit    : Screen("outfit",      "Tenues")
    object Calendar  : Screen("calendar",   "Calendrier")
    object Voyage    : Screen("voyage",      "Voyage")
    object Stats     : Screen("stats",       "Stats")
    object Settings  : Screen("settings",   "Paramètres")
    object AddArticle: Screen("add_article", "Ajouter")
}

private data class NavItem(val screen: Screen, val icon: ImageVector)

private val bottomItems = listOf(
    NavItem(Screen.Wardrobe, Icons.Outlined.GridView),
    NavItem(Screen.Outfit,   Icons.Outlined.AutoAwesome),
    NavItem(Screen.Calendar, Icons.Outlined.CalendarToday),
    NavItem(Screen.Voyage,   Icons.Outlined.FlightTakeoff),
    NavItem(Screen.Stats,    Icons.Outlined.Analytics),
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomItems.map { it.screen.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomItems.forEach { item ->
                        val isSelected = navBackStackEntry?.destination?.hierarchy
                            ?.any { it.route == item.screen.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.screen.label
                                )
                            },
                            label = {
                                Text(
                                    item.screen.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Wardrobe.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Wardrobe.route) {
                WardrobeScreen(
                    onAddClick = { navController.navigate(Screen.AddArticle.route) }
                )
            }
            composable(Screen.Outfit.route)   { OutfitScreen() }
            composable(Screen.Calendar.route) { CalendarScreen() }
            composable(Screen.Voyage.route)   { VoyageScreen() }
            composable(Screen.Stats.route)    { StatsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.AddArticle.route) {
                AddArticleScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}
