package raidhelper.dynamodb.converter

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import raidhelper.dynamodb.model.RaidModel
import raidhelper.dynamodb.record.RaidRecord

object RaidConverter {
    fun fromRecord(record: RaidRecord): RaidModel {
        return RaidModel(
            record.RaidID,
            record.RaidLeader,
            record.DateAndTime,
            record.Participants.map { it.s }, // Extract string values
            record.RoleComposition.mapKeys { it.key }.mapValues { it.value.n?.toInt() ?: 0 }, // Handle potential number conversions
            record.Status
        )
    }

    fun toRecord(raid: RaidModel): RaidRecord {
        return RaidRecord(
            raid.raidId,
            raid.raidLeader,
            raid.dateAndTime,
            raid.participants.map { AttributeValue().withS(it) },
            raid.roleComposition.mapKeys { it.key }.mapValues { AttributeValue().withN(it.value.toString()) },
            raid.status
        )
    }

    fun fromDdb(item: Map<String, AttributeValue>): RaidRecord {
        return RaidRecord(
            item["RaidID"]?.s ?: error("RaidID Missing"),
            item["RaidLeader"]?.s ?: error("RaidLeader Missing"),
            item["DateAndTime"]?.s ?: error("DateAndTime Missing"),
            item["Participants"]?.l?.map { AttributeValue().withS(it.s) }?.toList() ?: emptyList(),
            item["RoleComposition"]?.m?.mapKeys { it.key }?.mapValues { AttributeValue().withN(it.value.n ?: "0") } ?: emptyMap(), // Corrected type and default value.
            item["Status"]?.s ?: error("Status Missing")
        )
    }
}
