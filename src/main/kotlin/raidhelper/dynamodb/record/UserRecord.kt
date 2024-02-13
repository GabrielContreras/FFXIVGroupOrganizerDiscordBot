package raidhelper.dynamodb.record

import com.amazonaws.services.dynamodbv2.model.AttributeValue

data class UserRecord(
    val DiscordId: String,
    val CharacterName: String,
    val AvailableJobs: List<AttributeValue>,
    val WeeklyRaidLimit: String,
    val CurrentSignups: List<AttributeValue>
)

fun UserRecord.toMap(): Map<String, AttributeValue> {
    return mapOf(
        "DiscordId" to AttributeValue().withS(this.DiscordId),
        "CharacterName" to AttributeValue().withS(this.CharacterName),
        "AvailableJobs" to AttributeValue().withSS(this.AvailableJobs.map { it.s }),
        "WeeklyRaidLimit" to AttributeValue().withS(this.WeeklyRaidLimit),
        "CurrentSignups" to AttributeValue().withSS(this.CurrentSignups.map { it.s }),
    )
}