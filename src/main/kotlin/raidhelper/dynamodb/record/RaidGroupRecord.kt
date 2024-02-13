package raidhelper.dynamodb.record

import com.amazonaws.services.dynamodbv2.model.AttributeValue

data class RaidGroupRecord(
    val RaidGroupId: String,
    val RaidIDs: List<AttributeValue>,
    val UserIDs: List<AttributeValue>
)

fun RaidGroupRecord.toMap(): Map<String,AttributeValue> {
    return mapOf(
        "RaidGroupId" to AttributeValue().withS(this.RaidGroupId),
        "RaidIDs" to AttributeValue().withSS(this.RaidIDs.map { it.s }),
        "UserIDs" to AttributeValue().withSS(this.UserIDs.map { it.s }),
    )
}