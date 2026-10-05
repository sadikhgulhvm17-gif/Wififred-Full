package com.wififred.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "الرئيسية", Icons.Filled.Home)
    object Wifi : Screen("wifi", "الشبكات", Icons.Filled.Wifi)
    object Servers : Screen("servers", "الخوادم", Icons.Filled.Dns)
    object Split : Screen("split", "التطبيقات", Icons.Filled.Apps)
    object Stats : Screen("stats", "الأداء", Icons.Filled.Speed)
    object Logs : Screen("logs", "السجلات", Icons.Filled.Terminal)
    object Settings : Screen("settings", "الإعدادات", Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Wifi,
    Screen.Servers,
    Screen.Stats,
    Screen.Settings
)
