package com.example

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.models.HelloResponse
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class HelloConfigTest(
    @Autowired
    // 注意：By NameでBean解決するので、このフィールド名がFunction名と一致している必要がある
    private val helloFunction: (APIGatewayProxyRequestEvent) -> APIGatewayProxyResponseEvent
) {

    @MockBean
    private lateinit var helloService: HelloService

    @BeforeEach
    fun setup() {
        // Mock the HelloService to return a specific message if necessary
        `when`(helloService.getHelloMessage()).thenReturn("Hello, Mocked World!")
    }

    @Test
    fun `helloFunction should return a 200 response with hello message`() {

        // Arrange
        val request = APIGatewayProxyRequestEvent()

        // Act
        val response = helloFunction(request)

        // Assert
        assertEquals(200, response.statusCode)

        // Deserialize the JSON response body to HelloResponse
        val helloResponse = Json.decodeFromString<HelloResponse>(response.body)

        // Verify that the response body contains the expected message
        assertEquals("Hello, Mocked World!", helloResponse.value)
    }

    @Test
    fun `helloFunction should return a version header`() {

        // Arrange
        val request = APIGatewayProxyRequestEvent()

        // Act
        val response = helloFunction(request)

        // Assert
        assert("X-App-Version" in response.headers)
        assert(response.headers["X-App-Version"] != "")
    }
}