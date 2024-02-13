package raidhelper.dynamodb.dao

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.GetItemResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import raidhelper.dynamodb.record.RaidGroupRecord
import raidhelper.dynamodb.record.toMap

class RaidGroupDaoImplTest {
    val testRaidGroupId = "test-raid-group"
    val mockedDynamoDBClient: AmazonDynamoDB = mock()
    val mockedGetItemResult = mock<GetItemResult>()
    val testRaidGroupRecord = RaidGroupRecord(testRaidGroupId, listOf(),  listOf())

    init {
        whenever(mockedDynamoDBClient.getItem(any<GetItemRequest>())).thenReturn(mockedGetItemResult)
        whenever(mockedGetItemResult.item).thenReturn(testRaidGroupRecord.toMap())
    }

    @Test
    fun `Get Existing Raid Group Success`() {
        val raidGroupDaoImpl = RaidGroupDaoImpl(mockedDynamoDBClient)
        val retrievedGroup = raidGroupDaoImpl.getRaidGroup(testRaidGroupId)

        assertEquals(testRaidGroupId, retrievedGroup?.raidGroupId)
        // ... Asserts for raidIds, userIds
    }
}
