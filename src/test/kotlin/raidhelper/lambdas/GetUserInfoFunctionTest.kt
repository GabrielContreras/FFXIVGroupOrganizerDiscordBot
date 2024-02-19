package raidhelper.lambdas

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetUserInfoFunctionTest {
    @Test
    fun `test getUser lambda`() {
        val input: Map<String,Any> = mapOf("discordId" to "test-id")
        println(GetUserInfoFunction().handleRequest(input, null).user)
    }
}