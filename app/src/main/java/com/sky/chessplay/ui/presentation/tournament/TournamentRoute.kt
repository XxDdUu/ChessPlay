package com.sky.chessplay.ui.presentation.tournament

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sky.chessplay.navigation.Route

@Composable
fun TournamentRoute(
    navController: NavHostController
) {
    TournamentScreen(
        onTournamentClick = { tournamentId ->
            navController.navigate(Route.TournamentDetail.createRoute(tournamentId))
        },
        navController = navController
    )
}
