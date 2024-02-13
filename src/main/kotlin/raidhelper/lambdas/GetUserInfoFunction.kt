package raidhelper.lambdas

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import raidhelper.dynamodb.model.UserModel
import raidhelper.dynamodb.dao.UserDao
import raidhelper.dynamodb.dao.UserDaoImpl

@Serializable
data class GetUserInfoRequest(val discordId: String? = null)

data class GetUserInfoResponse(val user: UserModel?, val message: String? = null)

class GetUserInfoFunction : RequestHandler<Map<String,Any>, GetUserInfoResponse> { // Return type will be UserModel?
    private val userDao: UserDao = UserDaoImpl(AmazonDynamoDBClientBuilder.defaultClient())

    override fun handleRequest(input: Map<String,Any>, context: Context?): GetUserInfoResponse {
        println("Initial input:") // Emphasize logging
        println(input)     // Inspect the exact raw input sent from the Toolkit

        if (input.isEmpty()) { // Small input sanity check
            return GetUserInfoResponse(null, "Missing input data")
        }
        val discordId = input["discordId"] as? String ?: return GetUserInfoResponse(null, "Missing or invalid discordId")
        println("DiscordId: $discordId")
        try {
            val user = userDao.getUser(discordId)
            println("User: $user")
            return GetUserInfoResponse(user = user, message = if (user != null) "User info retrieved" else "User not found")
        } catch (e: Exception) {
            return GetUserInfoResponse(null, "Failed to parse request: ${e.message}")
        }
    }
}