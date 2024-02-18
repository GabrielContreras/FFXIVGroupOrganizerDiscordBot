package raidhelper.dynamodb.dao

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.*
import raidhelper.dynamodb.converter.UserConverter
import raidhelper.dynamodb.dao.UserDao
import raidhelper.dynamodb.model.UserModel
import raidhelper.dynamodb.record.toMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserDaoImpl(private val ddbClient: AmazonDynamoDB) : UserDao {
    private val tableName = System.getenv("USERS_TABLE_NAME") ?: "Users"

    override fun createUser(user: UserModel): Boolean {
        val userRecord = UserConverter().toDdb(user)
        val request = PutItemRequest(tableName, userRecord)
        try {
            ddbClient.putItem(request)
            return true
        } catch (e: Exception) {
            println("Failed to put item: $e")
            // Add robust error logging here
            return false
        }
    }

    override fun getUser(discordId: String): UserModel? {
        val key = mapOf("DiscordId" to AttributeValue().withS(discordId))
        val request = GetItemRequest(tableName, key)
        try {
            val result = ddbClient.getItem(request)
            return if (result.item != null) {
                UserConverter().toUser(result.item)
            } else {
                null
            }
        } catch (e: Exception) {
            // Add robust error logging here
            println("Failed to get user: $e")
            return null
        }
    }

    override fun updateUser(user: UserModel): Boolean {
        val userRecord = UserConverter().toDdb(user)
        val key = mapOf("DiscordId" to AttributeValue().withS(user.discordId))

        // Build update expression (efficiently modifies only certain attributes)
        val updateExpression = StringBuilder("SET ")
        val expressionAttributeValues = mutableMapOf<String, AttributeValue>()

        var needsComma = false
        for (prop in listOf("CharacterName", "AvailableJobs", "WeeklyRaidLimit", "CurrentSignups")) {
            if (needsComma) updateExpression.append(", ")
            updateExpression.append("$prop = :$prop")
            expressionAttributeValues[":$prop"] = if(prop == "AvailableJobs" || prop == "CurrentSignups"){
                AttributeValue().withL(userRecord[prop]?.l ?: emptyList())
            } else {
                AttributeValue().withS(userRecord[prop]?.s)
            }
            needsComma = true
        }

        val request = UpdateItemRequest()
            .withTableName(tableName)
            .withKey(key)
            .withUpdateExpression(updateExpression.toString())
            .withExpressionAttributeValues(expressionAttributeValues)
        try {
            ddbClient.updateItem(request)
            return true
        } catch (e: Exception) {
            println("Failed to update user: $e")
            return false
        }
    }

    override suspend fun deleteUser(discordId: String): Boolean {
        val key = mapOf("DiscordId" to AttributeValue().withS(discordId))

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