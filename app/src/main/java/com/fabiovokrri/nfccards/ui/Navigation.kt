package com.fabiovokrri.nfccards.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fabiovokrri.nfccards.ui.addmodify.AddModifyScreen
import com.fabiovokrri.nfccards.ui.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class AddModify(val cardId: Int? = null)

@Composable
fun NFCCardsNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateToAddModify = { cardId: Int? ->
                    navController.navigate(AddModify(cardId))
                }
            )
        }

        composable<AddModify> {
            AddModifyScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateUp = navController::navigateUp
            )
        }
    }
}