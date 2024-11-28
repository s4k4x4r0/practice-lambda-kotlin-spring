package com.example.handlerBuilder

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HandlerTest {

    @Serializable
    data class RequestBody(val name: String, val age: Int)

    @Serializable
    data class QueryParameters(val filter: String, val limit: Int)

    @Serializable
    data class ResponseBody(val message: String)

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `test createApiHandler`() {
        val handler = Handler.createApiHandler<RequestBody, ResponseBody> { requestBody ->
            assertEquals("John", requestBody.name)
            assertEquals(30, requestBody.age)
            ResponseBody("Hello, ${requestBody.name}")
        }

        val requestBody = json.encodeToString(RequestBody("John", 30))
        val requestEvent = APIGatewayProxyRequestEvent().apply {
            this.body = requestBody
        }

        val response = handler(requestEvent)

        assertEquals(200, response.statusCode)
        assertEquals(
            "{\"message\":\"Hello, John\"}",
            response.body
        )
    }

    @Test
    fun `test createApiHandlerWithoutBody`() {
        val handler = Handler.createApiHandlerWithoutBody<ResponseBody> {
            ResponseBody("Hello, World!")
        }

        val requestEvent = APIGatewayProxyRequestEvent()

        val response = handler(requestEvent)

        assertEquals(200, response.statusCode)
        assertEquals(
            "{\"message\":\"Hello, World!\"}",
            response.body
        )
    }

    @Test
    fun `test createApiHandlerWithoutBodyWithQueryParameter`() {
        val handler =
            Handler.createApiHandlerWithoutBodyWithQueryParameter<QueryParameters, ResponseBody> { queryParams ->
                assertEquals("active", queryParams.filter)
                assertEquals(10, queryParams.limit)
                ResponseBody("Filter: ${queryParams.filter}, Limit: ${queryParams.limit}")
            }

        val queryParameters = mapOf("filter" to "active", "limit" to "10")
        val requestEvent = APIGatewayProxyRequestEvent().apply {
            this.queryStringParameters = queryParameters
        }

        val response = handler(requestEvent)

        assertEquals(200, response.statusCode)
        assertEquals(
            "{\"message\":\"Filter: active, Limit: 10\"}",
            response.body
        )
    }

    @Test
    fun `test createApiHandlerWithQueryParameter`() {
        val handler =
            Handler.createApiHandlerWithQueryParameter<RequestBody, QueryParameters, ResponseBody> { requestBody, queryParams ->
                assertEquals("John", requestBody.name)
                assertEquals(30, requestBody.age)
                assertEquals("active", queryParams.filter)
                assertEquals(10, queryParams.limit)
                ResponseBody("Hello, ${requestBody.name}. Filter: ${queryParams.filter}")
            }

        val requestBody = json.encodeToString(RequestBody("John", 30))
        val queryParameters = mapOf("filter" to "active", "limit" to "10")
        val requestEvent = APIGatewayProxyRequestEvent().apply {
            this.body = requestBody
            this.queryStringParameters = queryParameters
        }

        val response = handler(requestEvent)

        assertEquals(200, response.statusCode)
        assertEquals(
            "{\"message\":\"Hello, John. Filter: active\"}",
            response.body
        )
    }
}