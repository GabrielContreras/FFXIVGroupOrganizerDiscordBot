package raidhelper.dynamodb.dao

import raidhelper.dynamodb.model.RaidGroupModel

interface RaidGroupDao {
    fun createRaidGroup(group: RaidGroupModel): Boolean
    fun getRaidGroup(raidGroupId: String): RaidGroupModel?
    fun updateRaidGroup(group: RaidGroupModel): Boolean
    suspend fun deleteRaidGroup(raidGroupId: String): Boolean
}