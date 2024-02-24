package raidhelper.dynamodb.model

data class RaidGroupModel(
    val raidGroupId: String,
    val groupName: String,
    val raidIds: List<String>,
    val userIds: List<String>
)