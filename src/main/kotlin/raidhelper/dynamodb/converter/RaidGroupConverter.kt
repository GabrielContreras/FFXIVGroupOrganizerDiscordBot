package raidhelper.dynamodb.converter

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import raidhelper.dynamodb.model.RaidGroupModel
import javax.management.Attribute

class RaidGroupConverter {
    fun toRaidGroup(record: Map<String,AttributeValue>): RaidGroupModel {
        return RaidGroupModel(
            record["OwnerId"]?.s ?: error("OwnerId could not be retrieved"),
            record["RaidGroupId"]?.s ?: error("RaidGroupId needs to be added"),
            record["GroupName"]?.s ?: error("GroupName needs to be added"),
            record["RaidIds"]?.l?.map { it.s } ?: listOf(),
            record["UserIds"]?.l?.map { it.s } ?: listOf(),
            record["Roles"]?.l?.map { it.s } ?: listOf(),
            convertSubRoles(record["SubRoles"]?.m) ?: emptyMap(),
        )
    }

    fun toDdb(raidGroup: RaidGroupModel): Map<String,AttributeValue> {
        return mapOf(
            "OwnerId" to AttributeValue().withS(raidGroup.ownerId),
            "RaidGroupId" to AttributeValue().withS(raidGroup.raidGroupId),
            "GroupName" to AttributeValue().withS(raidGroup.groupName),
            "RaidIds" to AttributeValue().withL(raidGroup.raidIds.map { AttributeValue().withS(it) }),
            "UserIds" to AttributeValue().withL(raidGroup.userIds.map { AttributeValue().withS(it) }),
            "Roles" to AttributeValue().withL(raidGroup.roles.map { AttributeValue().withS(it)}),
            "SubRoles" to AttributeValue().withM(
                raidGroup.subRoles.mapValues { (_,value) ->
                    AttributeValue().withL(value.map { AttributeValue().withS(it) })
                }
            )
        )
    }

    private fun convertSubRoles(subRolesMap: Map<String, AttributeValue>?): Map<String, List<String>>? {
        if (subRolesMap == null) return null

        val convertedSubRoles = mutableMapOf<String, List<String>>()
        for ((key, value) in subRolesMap) {
            if (value.l != null) {
                convertedSubRoles[key] = value.l.mapNotNull { it.s }  // Safely handle strings within the list
            }
        }
        return convertedSubRoles
    }
}