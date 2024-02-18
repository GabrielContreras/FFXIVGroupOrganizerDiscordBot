package raidhelper.dynamodb.converter

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.kms.model.NotFoundException
import raidhelper.dynamodb.model.UserModel
import raidhelper.dynamodb.record.UserRecord

class UserConverter {
    fun toUser(record: Map<String,AttributeValue>): UserModel {
        return UserModel(
            record["DiscordId"]?.s ?: error("DiscordID needs to be added."),
            record["CharacterName"]?.s ?: "",
            record["AvailableJobs"]?.l?.map { it.s } ?: listOf(),
            record["WeeklyRaidLimit"]?.n?.toIntOrNull() ?: 0, // Handle potential conversions
            record["CurrentSignups"]?.l?.map { it.s } ?: listOf(),
        )
    }

    fun toDdb(user: UserModel): Map<String,AttributeValue> {
        return mapOf(
            "DiscordId" to AttributeValue().withS(user.discordId),
            "CharacterName" to AttributeValue().withS(user.characterName),
            "AvailableJobs" to AttributeValue().withL(user.availableJobs.map { AttributeValue().withS(it) }),
            "WeeklyRaidLimit" to AttributeValue().withS(user.weeklyRaidLimit.toString()),
            "CurrentSignups" to AttributeValue().withL(user.currentSignups.map { AttributeValue().withS(it) }),
        )
    }
}