package com.closetmixer.android.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.closetmixer.android.ui.navigation.Screen

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(Screen.Wardrobe, Screen.Outfit, Screen.Calendar, Screen.Voyage, Screen.Stats)
    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screen.Wardrobe -> Icons.Default.Inventory2
                            Screen.Outfit -> Icons.Default.Checkroom
                            Screen.Calendar -> Icons.Default.CalendarMonth
                            Screen.Voyage -> Icons.Default.FlightTakeoff
                            else -> Icons.Default.BarChart
                        },
                        contentDescription = screen.label
                    )
                },
                label = { Text(screen.label) },
                selected = currentRoute == screen.route,
                onClick = { onNavigate(screen.route) }
            )
        }
    }
}
