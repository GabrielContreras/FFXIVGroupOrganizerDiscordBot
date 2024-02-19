package raidhelper.dynamodb.dao

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import raidhelper.dynamodb.converter.RaidConverter
import raidhelper.dynamodb.model.RaidModel
import raidhelper.dynamodb.record.toMap

class RaidDaoImpl(ddbClient: AmazonDynamoDB) : RaidDao {
    private val ddbClient = ddbClient
    private val tableName = System.getenv("RAIDS_TABLE_NAME") ?: "Raids"

    override fun createRaid(raid: RaidModel): Boolean {
        val raidRecord = RaidConverter.toDdb(raid)
        val request = PutItemRequest(tableName, raidRecord.toMap())
        try {
            ddbClient.putItem(request)
            return true
        } catch (e: Exception) {
            println("Failed to put item: $e")
            // Add robust error logging here
            return false
        }
    }

    override fun getRaid(raidId: String): RaidModel? {
        val key = mapOf("RaidID" to AttributeValue().withS(raidId))
        println(key)
        val request = GetItemRequest(tableName, key)
        println(request)
        try {
            val result = ddbClient.getItem(request)
            return if (result.item != null) {
                RaidConverter.toRaid(result.item) // Map toRaidRecord, then Conversion
            } else {
                null
            }
        } catch (e: Exception) {
            println("Failed to get raid item: $e")
            // Add robust error logging here
            return null
        }
    }

    override fun updateRaid(raid: RaidModel): Boolean {
        val raidRecord = RaidConverter.toDdb(raid)
        val key = mapOf("RaidID" to AttributeValue().withS(raid.raidId))

        // Build an update expression dynamically
        val updateExpression = StringBuilder("SET ")
        val expressionAttributeValues = mutableMapOf<String, AttributeValue>()
        val expressionAttributeNames = mutableMapOf<String,String>()

        var needsComma = false
        for(prop in listOf("RaidLeader", "Participants", "DateAndTime", "RoleComposition")) {
            if(needsComma) updateExpression.append(", ")
            updateExpression.append("$prop = :$prop")
            expressionAttributeValues[":$prop"] = if(prop == "Participants") {
                AttributeValue().withL(raidRecord[prop]?.l ?: emptyList())
            } else if (prop == "RoleComposition") {
                AttributeValue().withM(raidRecord[prop]?.m ?: emptyMap())
            } else {
                AttributeValue().withS(raidRecord[prop]?.s)
            }
            needsComma = true
        }

        if(needsComma) updateExpression.append(", ")
        updateExpression.append("#status = :status")
        expressionAttributeValues[":status"] = AttributeValue().withS(raidRecord["Status"]?.s)
        expressionAttributeNames["#status"] = "Status"

        println("Update expresion: $updateExpression")
        println("Expression attribute values: $expressionAttributeValues")
        println("Expression attribute names: $expressionAttributeNames")

        val request = UpdateItemRequest()
            .withTableName(tableName)
            .withKey(key)
            .withUpdateExpression(updateExpression.toString())
            .withExpressionAttributeValues(expressionAttributeValues)
            .withExpressionAttributeNames(expressionAttributeNames)
        try {
            ddbClient.updateItem(request)
            return true
        } catch (e: Exception) {
            // Add robust error logging here
            println("Failed to update raid: $e")
            return false
        }
    }

    override suspend fun deleteRaid(raidId: String): Boolean {
        val key = mapOf("RaidId" to AttributeValue().withS(raidId))
        val request = DeleteItemRequest()
            .withTableName(tableName)
            .withKey(key)

        return try {
            withContext(Dispatchers.IO) { ddbClient.deleteItem(request)}
            true
        } catch(e: AmazonDynamoDBException) {
            println("Failed to delete user: $e")
            false
        }
    }
}