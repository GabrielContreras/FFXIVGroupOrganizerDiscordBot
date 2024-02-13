package raidhelper.dynamodb.converter

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import raidhelper.dynamodb.model.UserModel
import raidhelper.dynamodb.record.UserRecord

class UserConverter {
    fun fromRecord(record: UserRecord): UserModel {
        return UserModel(
            record.DiscordId,
            record.CharacterName,
            record.AvailableJobs.map { it.s },
            record.WeeklyRaidLimit.toInt(), // Handle potential conversions
            record.CurrentSignups.map { it.s }
        )
    }

    fun toRecord(user: UserModel): UserRecord {
        return UserRecord(
            user.discordId,
            user.characterName,
            user.availableJobs.map { AttributeValue().withS(it) },
            user.weeklyRaidLimit.toString(),
            user.currentSignups.map { AttributeValue().withS(it) }
        )
    }

    fun fromDdb(item: Map<String, AttributeValue>): UserRecord {
        return UserRecord(
            item["DiscordId"]?.s ?: error("DiscordId Missing"),
            item["CharacterName"]?.s ?: error("CharacterName Missing"),
            item["AvailableJobs"]?.l?.map { AttributeValue().withS(it.s) } ?: emptyList(),
            item["WeeklyRaidLimit"]?.s ?: error("WeeklyRaidLimit Missing"),
            item["CurrentSignups"]?.l?.map { AttributeValue().withS(it.s) } ?: emptyList()
        )
    }

    fun toDB(user: UserModel, propertyName: String): AttributeValue {
        return when (propertyName) {
            "CharacterName" -> AttributeValue().withS(user.characterName)
            "AvailableJobs" -> AttributeValue().withL(user.availableJobs.map { AttributeValue().withS(it) })
            "WeeklyRaidLimit" -> AttributeValue().withS(user.weeklyRaidLimit.toString())
            "CurrentSignups" -> AttributeValue().withL(user.currentSignups.map { AttributeValue().withS(it) })
            else -> error("Invalid property name: $propertyName")
        }
    }
}