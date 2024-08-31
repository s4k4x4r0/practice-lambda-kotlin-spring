package com.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UppercaseServiceTest(
    @Autowired private val uppercaseService: UppercaseService
) {
    @Test
    fun `convertToUppercase should convert string to uppercase`() {
        val result = uppercaseService.convertToUppercase("hello")
        assertEquals("HELLO", result)
    }

    @Test
    fun `convertToUppercase should apply length limit`() {
        val result = uppercaseService.convertToUppercase("hello", lengthLimit = 3)
        assertEquals("HEL", result)
    }

    @Test
    fun `convertToUppercase should apply prefix`() {
        val result = uppercaseService.convertToUppercase("hello", applyPrefix = true, prefix = "Pre_")
        assertEquals("Pre_HELLO", result)
    }

    @Test
    fun `convertToUppercase should apply both length limit and prefix`() {
        val result = uppercaseService.convertToUppercase("hello", lengthLimit = 3, applyPrefix = true, prefix = "Pre_")
        assertEquals("Pre_HEL", result)
    }

    @Test
    fun `convertToUppercase should handle null prefix when applyPrefix is true`() {
        val result = uppercaseService.convertToUppercase("hello", applyPrefix = true, prefix = null)
        assertEquals("HELLO", result)
    }

    @Test
    fun `convertToUppercase should handle lengthLimit larger than input length`() {
        val result = uppercaseService.convertToUppercase("hello", lengthLimit = 10)
        assertEquals("HELLO", result)
    }

    @Test
    fun `convertToUppercase should return the original uppercase string when no options are applied`() {
        val result = uppercaseService.convertToUppercase("hello")
        assertEquals("HELLO", result)
    }
}