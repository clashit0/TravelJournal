package com.abhinav.traveljournal.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abhinav.traveljournal.presentation.JournalViewmodel
import com.abhinav.traveljournal.presentation.screens.AddJournalScreen
import com.abhinav.traveljournal.presentation.screens.HomeScreen
import com.abhinav.traveljournal.presentation.screens.JournalDetailScreen

@Composable
fun AppNavGraph(
    viewmodel: JournalViewmodel
){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ){
        composable(Routes.HOME){
            HomeScreen(
                viewmodel = viewmodel,
                onAddClick = {
                    navController.navigate(Routes.ADD_JOURNAL)
                },
                onJournalClick = {id ->
                    navController.navigate("${Routes.DETAIL}/$id")
                },
                onEdit = {journalEntity ->
                    navController.navigate("${Routes.ADD_JOURNAL}?id=${journalEntity.id}")
                }
            )
        }

        composable(
            route = "${Routes.ADD_JOURNAL}?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->

            val journalId = backStackEntry.arguments
                ?.getInt("id")
                ?.takeIf { it != -1 }

            AddJournalScreen(
                viewmodel = viewmodel,
                journalId = journalId,
                onSaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = "${Routes.DETAIL}/{journalId}") {backStackEntry ->
            val journalId = backStackEntry.arguments?.getString("journalId")?.toInt()?:return@composable

            JournalDetailScreen(
                journalId = journalId,
                viewmodel = viewmodel
            )
        }
    }
}