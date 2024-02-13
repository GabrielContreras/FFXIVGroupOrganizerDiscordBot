package raidhelper.dynamodb.dao

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import raidhelper.dynamodb.converter.RaidGroupConverter
import raidhelper.dynamodb.model.RaidGroupModel
import raidhelper.dynamodb.record.toMap

class RaidGroupDaoImpl(ddbClient: AmazonDynamoDB) : RaidGroupDao {
    private val ddbClient = ddbClient
    private val tableName = System.getenv("RAID_GROUPS_TABLE_NAME")

    override fun createRaidGroup(group: RaidGroupModel): Boolean {
        val groupRecord = RaidGroupConverter().toRecord(group)
        val request = PutItemRequest(tableName, groupRecord.toMap())

        try {
            ddbClient.putItem(request)
            return true
        } catch (e: Exception) {
            // Add robust error logging here
            return false
        }
    }

    override fun getRaidGroup(raidGroupId: String): RaidGroupModel? {
        val key = mapOf("RaidGroupId" to AttributeValue().withS(raidGroupId))
        val request = GetItemRequest(tableName, key)

        try {
            val result = ddbClient.getItem(request)
            return if (result.item != null) {
                RaidGroupConverter().fromRecord(RaidGroupConverter().fromDdb(result.item))
            } else {
                null
            }
        } catch (e: Exception) {
            // Add robust error logging here
            return null
        }
    }

    override fun addRaidToGroup(raidGroupId: String, raidId: String): Boolean {
        val key = mapOf("RaidGroupId" to AttributeValue().withS(raidGroupId))
        val expression = "SET RaidIDs = list_append(if_not_exists(RaidIDs, :empty_list), :r)"
        val expressionValues = mapOf(
            ":r" to AttributeValue().withSS(listOf(raidId)),
            ":empty_list" to AttributeValue().withSS()
        )

        val request = UpdateItemRequest()
            .withTableName(tableName)
            .withKey(key)
            .withUpdateExpression(expression)
            .withExpressionAttributeValues(expressionValues)

        try {
            ddbClient.updateItem(request)
            return true
        } catch (e: Exception) {
            // Add robust error logging here
            return false
        }
    }

    override fun addUserToGroup(raidGroupId: String, userId: String): Boolean {
        // Similar logic to addRaidToGroup, but updating the UserIDs attribute
        return false // Implement this
    }
}