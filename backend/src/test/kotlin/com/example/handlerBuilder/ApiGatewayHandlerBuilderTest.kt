package com.example.handlerBuilder

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApiGatewayHandlerBuilderTest {

    @Serializable
    data class Request(val name: String)

    @Serializable
    data class Query(val filter: String)

    @Serializable
    data class Response(val message: String)

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `build handler should return expected response for valid body and query`() {
        // Arrange
        val handler = ApiGatewayHandlerBuilder.build<Request, Query, Response> { body, query ->
            Response(message = "Hello, ${body.name}! Filter: ${query.filter}")
        }
        val requestBody = json.encodeToString(Request(name = "Test User"))
        val queryParameters = mapOf("filter" to "active")
        val requestEvent = APIGatewayProxyRequestEvent()
            .withBody(requestBody)
            .withQueryStringParameters(queryParameters)

        // Act
        val responseEvent = handler(requestEvent)

        // Assert
        assertEquals(200, responseEvent.statusCode)
        val expectedResponseBody = json.encodeToString(
            Response(message = "Hello, Test User! Filter: active")
        )
        assertEquals(expectedResponseBody, responseEvent.body)
    }

    @Test
    fun `build handler should return 400 for invalid body`() {
        // Arrange
        val handler = ApiGatewayHandlerBuilder.build<Request, Query, Response> { body, query ->
            Response(message = "Hello, ${body.name}! Filter: ${query.filter}")
        }
        val invalidRequestBody = """{"invalidField":"value"}""" // Invalid JSON for `Request`
        val queryParameters = mapOf("filter" to "active")
        val requestEvent = APIGatewayProxyRequestEvent()
            .withBody(invalidRequestBody)
            .withQueryStringParameters(queryParameters)

        // Act
        val responseEvent = handler(requestEvent)

        // Assert
        assertEquals(400, responseEvent.statusCode)
        val expectedErrorResponse = json.encodeToString(
            mapOf("ErrorMessage" to "不正なリクエストボディです")
        )
        assertEquals(expectedErrorResponse, responseEvent.body)
    }

    @Test
    fun `build handler should return 400 for invalid query`() {
        // Arrange
        val handler = ApiGatewayHandlerBuilder.build<Request, Query, Response> { body, query ->
            Response(message = "Hello, ${body.name}! Filter: ${query.filter}")
        }
        val requestBody = json.encodeToString(Request(name = "Test User"))
        val invalidQueryParameters = mapOf("invalidKey" to "value") // Invalid JSON for `Query`
        val requestEvent = APIGatewayProxyRequestEvent()
            .withBody(requestBody)
            .withQueryStringParameters(invalidQueryParameters)

        // Act
        val responseEvent = handler(requestEvent)

        // Assert
        assertEquals(400, responseEvent.statusCode)
        val expectedErrorResponse = json.encodeToString(
            mapOf("ErrorMessage" to "不正なクエリパラメータです")
        )
        assertEquals(expectedErrorResponse, responseEvent.body)
    }

    @Test
    fun `build handler should return 500 for exception during action`() {
        // Arrange
        val handler = ApiGatewayHandlerBuilder.build<Request, Query, Response> { _, _ ->
            throw RuntimeException("Unexpected error")
        }
        val requestBody = json.encodeToString(Request(name = "Test User"))
        val queryParameters = mapOf("filter" to "active")
        val requestEvent = APIGatewayProxyRequestEvent()
            .withBody(requestBody)
            .withQueryStringParameters(queryParameters)

        // Act
        val responseEvent = handler(requestEvent)

        // Assert
        assertEquals(500, responseEvent.statusCode)
        val expectedErrorResponse = json.encodeToString(
            mapOf("ErrorMessage" to "サーバエラーが発生しました")
        )
        assertEquals(expectedErrorResponse, responseEvent.body)
    }

    @Test
    fun `build handler should handle Unit for body and query`() {
        // Arrange
        val handler = ApiGatewayHandlerBuilder.build<Unit, Unit, Response> { _, _ ->
            Response(message = "Handled Unit")
        }
        val requestEvent = APIGatewayProxyRequestEvent()

        // Act
        val responseEvent = handler(requestEvent)

        // Assert
        assertEquals(200, responseEvent.statusCode)
        val expectedResponseBody = json.encodeToString(Response(message = "Handled Unit"))
        assertEquals(expectedResponseBody, responseEvent.body)
    }
}