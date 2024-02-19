package raidhelper.dynamodb.converter

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import raidhelper.dynamodb.model.RaidModel
import raidhelper.dynamodb.record.RaidRecord

object RaidConverter {
    fun toRaid(record: Map<String,AttributeValue>): RaidModel {
        return RaidModel(
            record["RaidID"]?.s ?: error("RaidID needs to be added"),
            record["RaidLeader"]?.s ?: error("RaidLeader needs to be added"),
            record["DateAndTime"]?.s ?: error("Date needs to be added"),
            record["Participants"]?.l?.map { it.s } ?: listOf(),
            record["RoleComposition"]?.m?.mapKeys { it.key }?.mapValues { it.value.n.toInt() } ?: emptyMap(),
            record["Status"]?.s ?: "Open",
        )
    }

    // TODO: Update RaidID to be uniform naming convention to out IDs: raidId
    fun toDdb(raid: RaidModel): Map<String,AttributeValue> {
        return mapOf(
            "RaidID" to AttributeValue().withS(raid.raidId),
            "RaidLeader" to AttributeValue().withS(raid.raidLeader),
            "DateAndTime" to AttributeValue().withS(raid.dateAndTime),
            "Participants" to AttributeValue().withL(raid.participants.map { AttributeValue().withS(it) }),
            "RoleComposition" to AttributeValue().withM(
                raid.roleComposition.mapValues { (_, value) -> AttributeValue().withN(value.toString()) }
            ),
            "Status" to AttributeValue().withS(raid.status),
        )
    }
}
