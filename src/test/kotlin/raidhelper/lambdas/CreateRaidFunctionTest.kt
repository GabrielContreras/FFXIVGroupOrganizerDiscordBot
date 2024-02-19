package raidhelper.lambdas

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CreateRaidFunctionTest {
    @Test
    fun testRaidCreation() {
        val inputMap = mapOf(
            "raidLeader" to "TestLeader",
            "dateAndTime" to "2024-02-21T20:00:00Z", // Example ISO 8601 format
            "participants" to listOf("PlayerA", "PlayerB", "PlayerC"),
            "roleComposition" to mapOf("Tank" to 1, "Healer" to 2, "DPS" to 3)
        )

        println(CreateRaidFunction().handleRequest(inputMap, null))
    }
}