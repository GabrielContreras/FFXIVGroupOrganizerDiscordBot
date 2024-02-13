package raidhelper.lambdas

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.kms.model.NotFoundException
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import raidhelper.dynamodb.dao.RaidDao
import raidhelper.dynamodb.dao.RaidDaoImpl

data class SelectParticipantsRequest(
    val raidId: String,
    val selectedParticipants: List<String>  // Assuming a list of DiscordIDs
)

data class SelectParticipantsResponse(
    val success: Boolean,
    val message: String? = null
)

// Assuming all functions use these basic imports for now
class SelectParticipantsFunction : RequestHandler<SelectParticipantsRequest, SelectParticipantsResponse> {
    private val raidDao: RaidDao = RaidDaoImpl(AmazonDynamoDBClientBuilder.defaultClient()) // Instantiate using your actual client

    override fun handleRequest(input: SelectParticipantsRequest, context: Context?): SelectParticipantsResponse {
        // TODO: Error handling for missing or invalid input

        // 1. Parse relevant raid details from input data
        val raidId = input.raidId
        val selectedParticipants = input.selectedParticipants

        // 2. Update raid information using RaidDao
        try {
            val existingRaid = raidDao.getRaid(raidId) ?: throw(NotFoundException("RaidID not found: $raidId"))

            val updatedRaid = existingRaid.copy(participants = selectedParticipants)

            val success = raidDao.updateRaid(updatedRaid)
            if (!success) {
                return SelectParticipantsResponse(false,"Error updating raid participants")
            }

            // TODO: Potentially update user records depending on your design

            // 3. Trigger your FinalResult lambda here
            invokeFinalResultLambda(raidId)

            return SelectParticipantsResponse(success = true)

        } catch (e: Exception) {
            //  Handle all potential exceptions related to RaidDao calls
            return SelectParticipantsResponse(false, "Error selecting participants: ${e.message}")
        }
    }

    private fun invokeFinalResultLambda(raidId: String) {
        // Implementation using AWS SDK for Lambda to trigger the 'FinalResult' function
        // TODO: Implement lambda client to invoke call to also create final result raid and push it back to designated channel
    }
}