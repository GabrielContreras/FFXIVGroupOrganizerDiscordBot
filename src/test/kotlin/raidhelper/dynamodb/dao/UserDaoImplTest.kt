package raidhelper.dynamodb.dao

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.GetItemRequest
import com.amazonaws.services.dynamodbv2.model.GetItemResult
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import raidhelper.dynamodb.model.UserModel
import raidhelper.dynamodb.record.UserRecord
import raidhelper.dynamodb.record.toMap

class UserDaoImplTest {
    val testDiscordId = "test-discord-user"
    val mockedDynamoDBClient: AmazonDynamoDB = mock()
    val mockedGetItemResult = mock<GetItemResult>()
    val testUserRecord = UserRecord(testDiscordId, "FFXIV Name", listOf(), "1", listOf()) // WeeklyRaidLimit as string now

    init {
        whenever(mockedDynamoDBClient.getItem(any<GetItemRequest>())).thenReturn(mockedGetItemResult)
        whenever(mockedGetItemResult.item).thenReturn(testUserRecord.toMap())
    }

//    @Test
//    fun `Get Existing User Success`() {
//        val userDaoImpl = UserDaoImpl(mockedDynamoDBClient)
//        val retrievedUser = userDaoImpl.getUser(testDiscordId)
//
//        assertEquals(testDiscordId, retrievedUser?.discordId)
//        assertEquals("FFXIV Name", retrievedUser?.characterName)
//        // ... Asserts for availableJobs
//        assertEquals(1, retrievedUser?.weeklyRaidLimit) // Now directly asserting the integer
//    }
//
//    @Test
//    fun `create user`() {
//        val userDaoImpl = UserDaoImpl(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build())
//        val userModel = UserModel(
//            discordId = "test-id-3",
//            characterName = "char name",
//            availableJobs = listOf("rdm"),
//            currentSignups = listOf("raid-3", "raid-1"),
//            weeklyRaidLimit = 10,
//        )
//        println(userDaoImpl.createUser(userModel))
//    }
//
//    @Test
//    fun `Testing update user`() {
//        val userDao = UserDaoImpl(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build())
//        val userModel = UserModel(
//            discordId = "test-id-3",
//            characterName = "char name2",
//            availableJobs = listOf("rdm", "mch"),
//            currentSignups = listOf("raid-2", "raid-1"),
//            weeklyRaidLimit = 5,
//        )
//        println(userDao.updateUser(userModel))
//    }
//
//    @Test
//    fun `Testing get user`() {
//        val testid = "test-id"
//        val userDao = UserDaoImpl(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build())
//        println(userDao.getUser(testid))
//    }
//
//    @Test
//    fun `Testing deleting user`() {
//        val testid = "test-id-3"
//        val userDao = UserDaoImpl(AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build())
//        runBlocking {
//            println(userDao.deleteUser(testid))
//        }
//    }
}