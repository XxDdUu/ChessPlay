data class TournamentRequest(
    val tournamentName: String,
    val description: String,
    val totalRounds: Int,
    val timeControl: String,
    val registrationStart: String,
    val registrationEnd: String,
    val startTime: String
)