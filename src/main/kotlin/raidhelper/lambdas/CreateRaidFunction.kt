package raidhelper.lambdas

import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import raidhelper.dynamodb.converter.RaidConverter
import raidhelper.dynamodb.model.RaidModel
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

// Input data class for Raid Creation Request
data class RaidCreationRequest(
    val raidLeader: String,
    val dateAndTime: String, // Could be represented as an ISO 8601 compliant date-time string
    val participants: List<String>,
    val roleComposition: Map<String,Int>,
)

// Response data class
data class RaidCreationResponse(val raidId: String, val success: Boolean)

class CreateRaidFunction : RequestHandler<Map<String,Any>, RaidCreationResponse> {
    private val dynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build()
    private val raidsTable = System.getenv("Raids") ?: "Raids"

    override fun handleRequest(request: Map<String,Any>, context: Context?): RaidCreationResponse {
        try {
            val mapper = ObjectMapper().registerKotlinModule()
            val jsonString = mapper.writeValueAsString(request)
            val raidCreationRequest: RaidCreationRequest = mapper.readValue(jsonString, RaidCreationRequest::class.java)

            // 1. Generate RaidID (using UUID)
            val raidId = UUID.randomUUID().toString()

            val raid = RaidModel(
                raidId = raidId,
                raidLeader = raidCreationRequest.raidLeader,
                dateAndTime = raidCreationRequest.dateAndTime,
                participants = raidCreationRequest.participants,
                roleComposition = raidCreationRequest.roleComposition,
                status = "Open",
            )
            // 2. Input Validation
            if (!isValidDateTime(raid.dateAndTime)) {
                return RaidCreationResponse(raidId = "", success = false) // Indicate Validation Failure
            }

            // 4. PutItem into Raids table
            dynamoDB.putItem(raidsTable, RaidConverter.toDdb(raid))

            return RaidCreationResponse(raidId, true)

        } catch (e: Exception) {
            context?.logger?.log("Error creating raid: ${e.message}")
            println("Failed to create raid: ${e.message}")
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
