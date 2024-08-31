package com.example

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.model.UppercaseRequest
import com.example.model.UppercaseResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.function.Function

@SpringBootTest
class UppercaseConfigTest(
    @Autowired private val uppercaseFunction: Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>
) {

    @MockBean
    private lateinit var uppercaseService: UppercaseService

    private val json = Json { ignoreUnknownKeys = true }

    @BeforeEach
    fun setup() {
        // Mock the UppercaseService to return a specific result
        `when`(uppercaseService.convertToUppercase("hello", 5, true, "Pre_")).thenReturn("Pre_HELLO")
    }

    @Test
    fun `uppercaseFunction should return a 200 response with correctly transformed uppercase string`() {
        // Arrange
        val request = UppercaseRequest(
            input = "hello",
            lengthLimit = 5,
            applyPrefix = true,
            prefix = "Pre_"
        )
        val requestBody = json.encodeToString(request)

        val apiGatewayRequest = APIGatewayProxyRequestEvent().withBody(requestBody)

        // Act
        val response = uppercaseFunction.apply(apiGatewayRequest)

        // Assert
        assertEquals(200, response.statusCode)

        val expectedResponse = UppercaseResponse(
            uppercase = "Pre_HELLO",
            original = "hello",
            length = 9
        )
        val expectedResponseBody = json.encodeToString(expectedResponse)

        assertEquals(expectedResponseBody, response.body)
    }
}