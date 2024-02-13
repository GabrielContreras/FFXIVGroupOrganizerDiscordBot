package raidhelper.lambdas

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.*
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

data class EndSignupRequest(val raidId: String)
data class EndSignupResponse(val success: Boolean)

// Assuming all functions use these basic imports for now
class EndSignupFunction : RequestHandler<EndSignupRequest, EndSignupResponse> {
    private val dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
    private val raidsTable = System.getenv("RAIDS_TABLE_NAME")

    override fun handleRequest(request: EndSignupRequest, context: Context): EndSignupResponse {
        try {
            // 1. Retrieve raid data by RaidID
            val raidItem = getRaidItem(request.raidId)
            if (raidItem == null) {
                return EndSignupResponse(success = false) // Raid not found
            }

            // 2. Check if the raid is still open for signups
            if (raidItem["Status"]?.s != "Open") {
                return EndSignupResponse(success = false) // Raid signup already closed
            }

            // 3. Update Raid Status to 'SignupEnded'
            updateRaidStatus(request.raidId, "Closed", context)

            return EndSignupResponse(success = true)
        } catch (e: Exception) {
            context.logger.log("Error ending signup: ${e.message}")
            return EndSignupResponse(success = false)
        }
    }

    // Helper function to retrieve raid details
    private fun getRaidItem(raidId: String): Map<String, AttributeValue>? {
        val keyConditionExpression = "RaidID = :rid"
        val expressionAttributeValues = hashMapOf<String, AttributeValue>(
            ":rid" to AttributeValue().withS(raidId)
        )

        val request = QueryRequest()
            .withTableName(raidsTable)
            .withKeyConditionExpression(keyConditionExpression)
            .withExpressionAttributeValues(expressionAttributeValues)

        val result = dynamoDB.query(request)
        return if (result.items.isNotEmpty()) {
            result.items[0]
        } else {
            null
        }
    }

    // Helper function to update the raid's status
    private fun updateRaidStatus(raidId: String, newStatus: String, context: Context) {
        val key = hashMapOf<String, AttributeValue>(
            "RaidID" to AttributeValue().withS(raidId)
        )

        val updateExpression = "SET Status = :s"
        val conditionExpression = "Status = :currentStatus" // Check for the existing status
        val expressionAttributeValues = hashMapOf<String, AttributeValue>(
            ":s" to AttributeValue().withS(newStatus),
            ":currentStatus" to AttributeValue().withS("Open")
        )

        val request = UpdateItemRequest()
            .withTableName(raidsTable)
            .withKey(key)
            .withUpdateExpression(updateExpression)
            .withConditionExpression(conditionExpression) // Our addition!
            .withExpressionAttributeValues(expressionAttributeValues)

        try {
            dynamoDB.updateItem(request)
        } catch (e: ConditionalCheckFailedException) {
            // Return an error response indicating the raid is already closed
            context.logger.log("Raid Update Failed (Raid Already Closed): $raidId")
        } catch (e: Exception) {
            context.logger.log("Error updating raid status: ${e.message}")
            // Return a basic error response (you could customize details exposed to the user)
        }
    }

}