package com.example.steptracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.steptracker.ui.agenda.AgendaDestination
import com.example.steptracker.ui.agenda.AgendaScreen
import com.example.steptracker.ui.home.HomeDestination
import com.example.steptracker.ui.home.HomeScreen
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneLayoutNav
import com.microsoft.device.dualscreen.twopanelayout.TwoPaneMode
import com.microsoft.device.dualscreen.twopanelayout.twopanelayoutnav.composable

interface NavigationDestination {
    val route: String
    val titleResourceId: Int
}

@Composable
fun NavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    TwoPaneLayoutNav(
        navController = navController,
        paneMode = TwoPaneMode.HorizontalSingle,
        singlePaneStartDestination = HomeDestination.route,
        pane1StartDestination = HomeDestination.route,
        pane2StartDestination = AgendaDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(isSinglePane)
        }
        composable(route = AgendaDestination.route) {
            if (isSinglePane) navController.navigateBack()
            else AgendaScreen(isSinglePane)
        }
    }
}