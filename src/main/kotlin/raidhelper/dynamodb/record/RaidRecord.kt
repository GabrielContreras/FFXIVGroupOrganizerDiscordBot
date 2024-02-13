package raidhelper.dynamodb.record

import com.amazonaws.services.dynamodbv2.model.AttributeValue

data class RaidRecord(
    val RaidID: String,
    val RaidLeader: String,
    val DateAndTime: String,
    val Participants: List<AttributeValue>, // Using AttributeValue for flexibility
    val RoleComposition: Map<String, AttributeValue>,
    val Status: String
)

// Assuming this is inside the RaidRecord.kt file, or in a file containing utility extensions for RaidRecord
fun RaidRecord.toMap(): Map<String, AttributeValue> {
    return mapOf(
        "RaidID" to AttributeValue().withS(this.RaidID),
        "RaidLeader" to AttributeValue().withS(this.RaidLeader),
        "DateAndTime" to AttributeValue().withS(this.DateAndTime),
        "Participants" to AttributeValue().withSS(this.Participants.map { it.s }),
        "RoleComposition" to AttributeValue().withM(this.RoleComposition.mapKeys { it.key }
            .mapValues { AttributeValue().withN(it.value.toString()) }), // Nested conversion required
        "Status" to AttributeValue().withS(this.Status)
    )
}