package com.reign.loomi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reign.loomi.ui.screen.HomeRoute
import com.reign.loomi.viewmodel.LoomiViewModel

object LoomiDestination {
    const val HOME = "home"
}

@Composable
fun LoomiNavGraph(viewModel: LoomiViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoomiDestination.HOME,
    ) {
        composable(LoomiDestination.HOME) {
            HomeRoute(viewModel = viewModel)
        }
    }
}
