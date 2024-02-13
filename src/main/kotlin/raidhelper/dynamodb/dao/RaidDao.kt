package raidhelper.dynamodb.dao

import raidhelper.dynamodb.model.RaidModel

interface RaidDao {
    fun createRaid(raid: RaidModel): Boolean
    fun getRaid(raidId: String): RaidModel?
    fun updateRaid(raid: RaidModel): Boolean
}