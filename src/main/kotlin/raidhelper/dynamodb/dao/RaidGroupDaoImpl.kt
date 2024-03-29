package raidhelper.dynamodb.dao

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import raidhelper.dynamodb.converter.RaidGroupConverter
import raidhelper.dynamodb.model.RaidGroupModel

class RaidGroupDaoImpl(private val ddbClient: AmazonDynamoDB) : RaidGroupDao {
    private val tableName = System.getenv("RAID_GROUPS_TABLE_NAME") ?: "RaidGroups"

    override fun createRaidGroup(group: RaidGroupModel): Boolean {
        val groupRecord = RaidGroupConverter().toDdb(group)
        val request = PutItemRequest(tableName, groupRecord)
        try {
            ddbClient.putItem(request)
            return true
        } catch (e: Exception) {
            // Add robust error logging here
            println("Failed to put item: $e")
            return false
        }
    }

    override fun getRaidGroup(raidGroupId: String): RaidGroupModel? {
        val key = mapOf("RaidGroupId" to AttributeValue().withS(raidGroupId))
        val request = GetItemRequest(tableName, key)
        try {
            val result = ddbClient.getItem(request)
            return if (result.item != null) {
                RaidGroupConverter().toRaidGroup(result.item)
            } else {
                null
            }
        } catch (e: Exception) {
            // Add robust error logging here
            println("Failed to get raid group: $e")
            return null
        }
    }

    override fun updateRaidGroup(raidGroup: RaidGroupModel): Boolean {
        val raidGroupRecord = RaidGroupConverter().toDdb(raidGroup)
        val key = mapOf("RaidGroupId" to AttributeValue().withS(raidGroup.raidGroupId))

        val updateExpression = StringBuilder("SET ")
        val expressionAttributeValues = mutableMapOf<String,AttributeValue>()
        val expressionAttributeNames = mutableMapOf<String,String>()

        var needsComma = false
        for(prop in listOf("OwnerId","GroupName", "RaidIds", "UserIds", "SubRoles")) {
            if(needsComma) updateExpression.append(", ")
            updateExpression.append("$prop = :$prop")
            expressionAttributeValues[":$prop"] = if(prop == "RaidIds" || prop == "UserIds") {
                AttributeValue().withL(raidGroupRecord[prop]?.l ?: emptyList())
            } else if(prop == "SubRoles") {
                AttributeValue().withM(raidGroupRecord[prop]?.m ?: emptyMap())
            } else {
                AttributeValue().withS(raidGroupRecord[prop]?.s)
            }
            needsComma = true
        }

        if(needsComma) updateExpression.append(", ")
        updateExpression.append("#roles = :roles")
        expressionAttributeValues[":roles"] = AttributeValue().withL(raidGroupRecord["Roles"]?.l ?: emptyList())
        expressionAttributeNames["#roles"] = "Roles"
        println("Expression update: ${expressionAttributeValues}")

        val request = UpdateItemRequest()
            .withTableName(tableName)
            .withKey(key)
            .withUpdateExpression(updateExpression.toString())
            .withExpressionAttributeValues(expressionAttributeValues)
            .withExpressionAttributeNames(expressionAttributeNames)

        println("Update Request: $request")

        try {
            ddbClient.updateItem(request)
            return true
        } catch (e: Exception) {
            // Add robust error logging here
            println("Failed to update raid group: $e")
            return false
        }
    }

    override suspend fun deleteRaidGroup(raidGroupId: String): Boolean {
        val key = mapOf("RaidGroupId" to AttributeValue().withS(raidGroupId))
        val request = DeleteItemRequest()
            .withTableName(tableName)
            .withKey(key)

        return try {
            withContext(Dispatchers.IO) { ddbClient.deleteItem(request) }
            true
        } catch(e: AmazonDynamoDBException) {
            println("Failed to delete user: $e")
            false
        }
    }
}