package raidhelper.lambdas

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.kms.model.NotFoundException
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import raidhelper.dynamodb.dao.RaidGroupDao
import raidhelper.dynamodb.dao.RaidGroupDaoImpl
import raidhelper.dynamodb.model.RaidGroupModel

data class AddRaidGroupRoleRequest(
    val raidId: String,
    val groupRole: String,
    val subRoles: List<String>
)

data class AddRaidGroupRoleResponse(
    val raidGroup: RaidGroupModel?,
    val message: String,
    val success: Boolean,
)
class AddRaidGroupRole: RequestHandler<Map<String, Any>, AddRaidGroupRoleResponse> {
  private val raidGroupDao: RaidGroupDao = RaidGroupDaoImpl(
      AmazonDynamoDBClientBuilder
          .standard()
          .withRegion(Regions.US_EAST_1)
          .build()
  )

    override fun handleRequest(input: Map<String, Any>?, context: Context?): AddRaidGroupRoleResponse {
        context?.logger?.log("Incoming request: $input")

        val mapper = ObjectMapper().registerKotlinModule()
        val jsonString = mapper.writeValueAsString(input)
        val addRaidGroupRoleRequest: AddRaidGroupRoleRequest = mapper.readValue(jsonString, AddRaidGroupRoleRequest::class.java)
        context?.logger?.log("Translated Raid Group Request: $addRaidGroupRoleRequest")

        val currentGroup = raidGroupDao.getRaidGroup(addRaidGroupRoleRequest.raidId)
            ?: throw(Exception("Raid group could not be found. ${addRaidGroupRoleRequest.raidId}"))
        context?.logger?.log("Existing raid group: $currentGroup")

        val newRole = addRaidGroupRoleRequest.groupRole
        val updatedRoles = currentGroup.roles.toMutableList()
        if(!updatedRoles.contains(newRole)) {
            updatedRoles.add(newRole)
            context?.logger?.log("Role $newRole does not currently exist. Adding to role list")
        }

        val newSubRoles = addRaidGroupRoleRequest.subRoles
        val updatedSubRoles = currentGroup.subRoles.toMutableMap()
        updatedSubRoles[newRole] = newSubRoles

        val newRaidGroup = currentGroup.copy(
            roles = updatedRoles,
            subRoles = updatedSubRoles,
        )

        context?.logger?.log("New Raid Group: $newRaidGroup")
        try {
            raidGroupDao.updateRaidGroup(newRaidGroup)
            context?.logger?.log("Successfully updated raid group with new role/subroles.")
            return AddRaidGroupRoleResponse(
                raidGroup = newRaidGroup,
                message = "Successfully updated role list",
                success = true,
            )
        } catch(e: Exception) {
            context?.logger?.log("Failed to update raid group with new role. ${e.message}")
            return AddRaidGroupRoleResponse(
                raidGroup = null,
                message = "Failed to update role list to DDB.",
                success = false,
            )
        }
    }
}