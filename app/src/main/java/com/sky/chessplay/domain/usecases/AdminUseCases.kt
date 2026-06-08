package com.sky.chessplay.domain.usecases

import com.sky.chessplay.domain.repository.AdminRepository
import javax.inject.Inject

class GetAdminDashboardUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke() = repository.getDashboardStats()
}

class GetAdminUsersUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(query: String? = null) = repository.getUsers(query)
}

class GetUserProfileAdminUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(userId: Long) = repository.getUserProfile(userId)
}

class BanUserUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(userId: Long) = repository.banUser(userId)
}

class UnbanUserUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(userId: Long) = repository.unbanUser(userId)
}

class GetAdminTournamentsUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke() = repository.getTournaments()
}

class FinishTournamentUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(tournamentId: Long) = repository.finishTournament(tournamentId)
}

class StartTournamentUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(tournamentId: Long) = repository.startTournament(tournamentId)
}

class CancelTournamentUseCase @Inject constructor(
    private val repository: AdminRepository
) {
    suspend operator fun invoke(tournamentId: Long) = repository.cancelTournament(tournamentId)
}
