package raidhelper.dynamodb.converter

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import raidhelper.dynamodb.model.RaidGroupModel
import raidhelper.dynamodb.record.RaidGroupRecord

class RaidGroupConverter {
    fun fromRecord(record: RaidGroupRecord): RaidGroupModel {
        return RaidGroupModel(
            record.RaidGroupId,
            record.RaidIDs.map { it.s },
            record.UserIDs.map { it.s }
        )
    }

    fun toRecord(group: RaidGroupModel): RaidGroupRecord {
        return RaidGroupRecord(
            group.raidGroupId,
            group.raidIds.map { AttributeValue().withS(it) },
            group.userIds.map { AttributeValue().withS(it) }
        )
    }

    fun fromDdb(item: Map<String, AttributeValue>): RaidGroupRecord {
        return RaidGroupRecord(
            item["RaidGroupId"]?.s ?: error("RaidGroupId Missing"),
            item["RaidIDs"]?.l?.map { AttributeValue().withS(it.s) }?.toList() ?: emptyList(),
            item["UserIDs"]?.l?.map { AttributeValue().withS(it.s) }?.toList() ?: emptyList()
        )
    }
}