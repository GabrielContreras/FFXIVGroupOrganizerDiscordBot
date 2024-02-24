package raidhelper.dynamodb.converter

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import org.w3c.dom.Attr
import raidhelper.dynamodb.model.RaidGroupModel
import raidhelper.dynamodb.record.RaidGroupRecord

class RaidGroupConverter {
    fun toRaidGroup(record: Map<String,AttributeValue>): RaidGroupModel {
        return RaidGroupModel(
            record["RaidGroupId"]?.s ?: error("RaidGroupId needs to be added"),
            record["GroupName"]?.s ?: error("GroupName needs to be added"),
            record["RaidIds"]?.l?.map { it.s } ?: listOf(),
            record["UserIds"]?.l?.map { it.s } ?: listOf(),
        )
    }

    fun toDdb(raidGroup: RaidGroupModel): Map<String,AttributeValue> {
        return mapOf(
            "RaidGroupId" to AttributeValue().withS(raidGroup.raidGroupId),
            "GroupName" to AttributeValue().withS(raidGroup.groupName),
            "RaidIds" to AttributeValue().withL(raidGroup.raidIds.map { AttributeValue().withS(it) }),
            "UserIds" to AttributeValue().withL(raidGroup.userIds.map { AttributeValue().withS(it) }),
        )
    }
}