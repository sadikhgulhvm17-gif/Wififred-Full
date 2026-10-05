package com.wififred.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wififred.app.presentation.screens.home.HomeScreen
import com.wififred.app.presentation.screens.logs.LogsScreen
import com.wififred.app.presentation.screens.servers.ServersScreen
import com.wififred.app.presentation.screens.settings.SettingsScreen
import com.wififred.app.presentation.screens.split.SplitTunnelingScreen
import com.wififred.app.presentation.screens.stats.StatsScreen
import com.wififred.app.presentation.screens.wifi.WifiScreen

@Composable
fun WififredNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Wifi.route) { WifiScreen() }
        composable(Screen.Servers.route) { ServersScreen() }
        composable(Screen.Split.route) { SplitTunnelingScreen() }
        composable(Screen.Stats.route) { StatsScreen() }
        composable(Screen.Logs.route) { LogsScreen() }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
    }
}
