package raidhelper.dynamodb.dao

import raidhelper.dynamodb.model.RaidGroupModel

interface RaidGroupDao {
    fun createRaidGroup(group: RaidGroupModel): Boolean
    fun getRaidGroup(raidGroupId: String): RaidGroupModel?
    fun addRaidToGroup(raidGroupId: String, raidId: String): Boolean
    fun addUserToGroup(raidGroupId: String, userId: String): Boolean
}