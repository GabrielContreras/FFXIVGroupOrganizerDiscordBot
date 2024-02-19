package raidhelper.lambdas

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EndSignupFunctionTest {
    @Test
    fun testEndingOpenRaid() {
        val inputMap = mapOf(
            "raidID" to "c12cba0b-76ad-4123-a343-f7382bdf840c"
        )
        EndSignupFunction().handleRequest(inputMap, null)
    }
}