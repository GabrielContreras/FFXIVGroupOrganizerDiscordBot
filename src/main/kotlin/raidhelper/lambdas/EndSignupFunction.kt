package raidhelper.lambdas

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.*
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import raidhelper.dynamodb.dao.RaidDaoImpl

data class EndSignupRequest(val raidID: String)
data class EndSignupResponse(val success: Boolean)

// Assuming all functions use these basic imports for now
class EndSignupFunction : RequestHandler<Map<String,Any>, EndSignupResponse> {
    private val dynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build()

    override fun handleRequest(request: Map<String,Any>, context: Context?): EndSignupResponse {
        try {
            val mapper = ObjectMapper().registerKotlinModule()
            val jsonString = mapper.writeValueAsString(request)
            val endSignupRequest: EndSignupRequest = mapper.readValue(jsonString, EndSignupRequest::class.java)

            println(endSignupRequest)

            // 1. Retrieve raid data by RaidID
            val raidItem = RaidDaoImpl(dynamoDB).getRaid(endSignupRequest.raidID)
                ?: return EndSignupResponse(success = false) // Raid not found

            // 2. Check if the raid is still open for signups
            if (raidItem.status != "Open") {
                return EndSignupResponse(success = false) // Raid signup already closed
            }

            // 3. Update Raid Status to 'SignupEnded'
            val updatedRaidItem = raidItem.copy(status = "Closed")
            RaidDaoImpl(dynamoDB).updateRaid(updatedRaidItem)

            return EndSignupResponse(success = true)
        } catch (e: Exception) {
            context?.logger?.log("Error ending signup: ${e.message}")
            println("Error ending signup: ${e.message}")
            return EndSignupResponse(success = false)
        }
    }

}