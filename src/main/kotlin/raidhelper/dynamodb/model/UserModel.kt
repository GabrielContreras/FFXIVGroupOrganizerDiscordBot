package raidhelper.dynamodb.model

data class UserModel(
    val discordId: String,
    val characterName: String,
    val availableJobs: List<String>,
    val weeklyRaidLimit: Int,
    val currentSignups: List<String> // Assuming Raid IDs
)