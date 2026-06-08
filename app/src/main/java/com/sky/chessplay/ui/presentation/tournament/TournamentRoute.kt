package com.sky.chessplay.ui.presentation.tournament

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun TournamentRoute(
    navController: NavHostController
) {
    TournamentScreen(
        onTournamentClick = { tournamentId ->
            navController.navigate("tournament_standings/$tournamentId")
        },
        onNavigateBack = {
            navController.popBackStack()
        },
        navController = navController
    )
}
