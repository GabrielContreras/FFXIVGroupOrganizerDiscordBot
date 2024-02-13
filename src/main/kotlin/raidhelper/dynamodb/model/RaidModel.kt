package raidhelper.dynamodb.model

data class RaidModel(
    val raidId: String,
    val raidLeader: String,
    val dateAndTime: String,
    val participants: List<String>,
    val roleComposition: Map<String, Int>, // Job Role -> Count
    val status: String,
)
