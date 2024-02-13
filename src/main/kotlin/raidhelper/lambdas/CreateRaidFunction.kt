package raidhelper.lambdas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

// Input data class for Raid Creation Request
data class RaidCreationRequest(
    val raidLeader: String,
    val dateAndTime: String, // Could be represented as an ISO 8601 compliant date-time string
    val raidName: String // To make it descriptive and add flexibility as needed
)

// Response data class
data class RaidCreationResponse(val raidId: String, val success: Boolean)

class CreateRaidFunction : RequestHandler<RaidCreationRequest, RaidCreationResponse> {
    private val dynamoDB = AmazonDynamoDBClientBuilder.defaultClient()
    private val raidsTable = System.getenv("Raids")

    override fun handleRequest(request: RaidCreationRequest, context: Context): RaidCreationResponse {
        try {
            // 1. Generate RaidID (using UUID)
            val raidId = UUID.randomUUID().toString()

            // 2. Input Validation
            if (!isValidDateTime(request.dateAndTime)) {
                return RaidCreationResponse(raidId = "", success = false) // Indicate Validation Failure
            }

            // 3. Construct DynamoDB Item
            val item = mutableMapOf<String, AttributeValue>()
            item["RaidID"] = AttributeValue(raidId)
            item["RaidLeader"] = AttributeValue(request.raidLeader)
            item["DateAndTime"] = AttributeValue(request.dateAndTime)
            item["RaidName"] = AttributeValue(request.raidName)
            item["Participants"] = AttributeValue().withSS(emptyList()) // Empty list initially
            item["JobComposition"] = AttributeValue().withM(emptyMap()) // Empty map initially
            item["Status"] = AttributeValue("Open") // Initially, the raid is open

            // 4. PutItem into Raids table
            dynamoDB.putItem(raidsTable, item)

            return RaidCreationResponse(raidId, true)

        } catch (e: Exception) {
            context.logger.log("Error creating raid: ${e.message}")
            return RaidCreationResponse(raidId = "", success = false) // Indicate Error
        }
    }

    // Helper function for basic date/time validation (YYYY-MM-DDThh:mm:ssZ)
    private fun isValidDateTime(dateAndTime: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            formatter.parse(dateAndTime)
            true // Parsing succeeded
        } catch (e: DateTimeParseException) {
            false // Parsing failed
        }
    }
}
