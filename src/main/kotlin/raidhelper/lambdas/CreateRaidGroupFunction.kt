package raidhelper.lambdas

import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.runtime.RequestHandler
import raidhelper.dynamodb.dao.RaidGroupDao
import raidhelper.dynamodb.dao.RaidGroupDaoImpl
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import raidhelper.dynamodb.model.RaidGroupModel
import java.util.*

data class CreateRaidGroupRequest(
    val callerId: String,
    val groupName: String,
)
data class CreateRaidGroupResponse(
    val raidGroup: RaidGroupModel?,
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
        val mapper = ObjectMapper().registerKotlinModule()
        val jsonString = mapper.writeValueAsString(input)
        val raidCreationRequest: CreateRaidGroupRequest = mapper.readValue(jsonString, CreateRaidGroupRequest::class.java)

        val raidGroupId = UUID.randomUUID().toString()

        val raidGroup = RaidGroupModel(
            ownerId = raidCreationRequest.callerId,
            raidGroupId = raidGroupId,
            groupName = raidCreationRequest.groupName,
            userIds = emptyList(),
            raidIds = emptyList(),
            roles = emptyList(),
            subRoles = emptyMap(),
        )

        try {
            raidGroupDao.createRaidGroup(raidGroup)
            return CreateRaidGroupResponse(raidGroup,true)
        } catch (e: Exception) {
            context?.logger?.log("Error creating raid group: ${e.message}")
            return CreateRaidGroupResponse(null, false)
        }
    }
}
