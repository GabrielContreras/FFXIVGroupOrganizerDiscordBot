package raidhelper.dynamodb.model

data class RaidGroupModel(
    val ownerId: String,
    val raidGroupId: String,
    val groupName: String,
    val raidIds: List<String>,
    val userIds: List<String>,
    val roles: List<String>,
    val subRoles: Map<String,List<String>>,
)