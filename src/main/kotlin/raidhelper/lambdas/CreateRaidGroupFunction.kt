package org.example.raidhelper.lambdas

import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.runtime.RequestHandler
import raidhelper.dynamodb.dao.RaidGroupDao
import raidhelper.dynamodb.dao.RaidGroupDaoImpl
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.lambda.runtime.Context
import raidhelper.dynamodb.model.RaidGroupModel
import java.util.*


data class CreateRaidGroupResponse(
    val raidGroupId: String,
    val success: Boolean,
)
class CreateRaidGroupFunction: RequestHandler<Map<String, Any>, CreateRaidGroupResponse> {
    private val raidGroupDao: RaidGroupDao = RaidGroupDaoImpl(
    AmazonDynamoDBClientBuilder
        .standard()
        .withRegion(Regions.US_EAST_1)
        .build()
    )

    override fun handleRequest(input: Map<String, Any>, context: Context?): CreateRaidGroupResponse {
        println("Initial input: ")
        println(input)
        val raidGroupId = UUID.randomUUID().toString()

        val raidGroup: RaidGroupModel = RaidGroupModel(
            raidGroupId = raidGroupId,
            userIds = listOf(input["discordId"] as? String ?: return CreateRaidGroupResponse("", false)),
            raidIds = emptyList(),
        )
        println("Raid Group: $raidGroup")
        try {
            raidGroupDao.createRaidGroup(raidGroup)
            return CreateRaidGroupResponse(raidGroupId,true)
        } catch (e: Exception) {
            context?.logger?.log("Error creating raid group: ${e.message}")
            return CreateRaidGroupResponse("", false)
        }
    }

}