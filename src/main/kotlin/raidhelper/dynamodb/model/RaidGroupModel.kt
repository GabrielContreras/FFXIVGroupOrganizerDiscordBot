package raidhelper.dynamodb.model

data class RaidGroupModel(
    val raidGroupId: String,
    val raidIds: List<String>,
    val userIds: List<String>
)