package raidhelper.dynamodb.dao

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import raidhelper.dynamodb.converter.RaidConverter
import raidhelper.dynamodb.model.RaidModel
import raidhelper.dynamodb.record.toMap

class RaidDaoImpl(ddbClient: AmazonDynamoDB) : RaidDao {
    private val ddbClient = ddbClient
    private val tableName = System.getenv("RAIDS_TABLE_NAME")

    override fun createRaid(raid: RaidModel): Boolean {
        val raidRecord = RaidConverter.toRecord(raid)
        val request = PutItemRequest(tableName, raidRecord.toMap())

        try {
            ddbClient.putItem(request)
            return true
        } catch (e: Exception) {
            // Add robust error logging here
            return false
        }
    }

    override fun getRaid(raidId: String): RaidModel? {
        val key = mapOf("RaidID" to AttributeValue().withS(raidId))
        val request = GetItemRequest(tableName, key)

        try {
            val result = ddbClient.getItem(request)
            return if (result.item != null) {
                RaidConverter.fromRecord(RaidConverter.fromDdb(result.item)) // Map toRaidRecord, then Conversion
            } else {
                null
            }
        } catch (e: Exception) {
            // Add robust error logging here
            return null
        }
    }

    override fun updateRaid(raid: RaidModel): Boolean {
        val key = mapOf("RaidID" to AttributeValue().withS(raid.raidId))

        // Build an update expression dynamically
        val updateExpression = buildUpdateExpression()
        val expressionValues = buildExpressionAttributeValues(raid)

        val request = UpdateItemRequest()
            .withTableName(tableName)
            .withKey(key)
            .withUpdateExpression(updateExpression)
            .withExpressionAttributeValues(expressionValues)

        try {
            ddbClient.updateItem(request)
            return true
        } catch (e: Exception) {
            // Add robust error logging here
            return false
        }
    }

    // Helper functions to build the update expression and value map
    private fun buildUpdateExpression(): String {
        val expressions = mutableListOf<String>()
        expressions.add("SET participants = :p") // Always update participants? Or conditional?
        expressions.add("SET raidLeader = :l")
        expressions.add("SET dateAndTime = :d")
        expressions.add("SET roleComposition = :r")
        expressions.add("SET status = :s")

        return expressions.joinToString(", ")
    }

    private fun buildExpressionAttributeValues(raid: RaidModel): MutableMap<String, AttributeValue> {
        val values = mutableMapOf<String, AttributeValue>()
        values[":p"] = AttributeValue().withSS(raid.participants)
        values[":l"] = AttributeValue().withS(raid.raidLeader)
        values[":d"] = AttributeValue().withS(raid.dateAndTime)
        values[":r"] = AttributeValue().withM(raid.roleComposition.mapValues { AttributeValue().withN(it.value.toString()) })
        values[":s"] = AttributeValue().withS(raid.status)
        return values
    }
}