package raidhelper.dynamodb.dao

import raidhelper.dynamodb.model.UserModel

interface UserDao {
    fun createUser(user: UserModel): Boolean
    fun getUser(discordId: String): UserModel?
    fun updateUser(user: UserModel): Boolean
    suspend fun deleteUser(discordId: String): Boolean
}