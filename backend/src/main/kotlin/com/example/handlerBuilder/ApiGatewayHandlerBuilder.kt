package com.example.handlerBuilder

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.buildConfig.BuildConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ApiGatewayHandlerBuilder<RequestBody, QueryParameters, ResponseBody>(
    private val httpHeaders: Map<String, String> = mapOf(),
) {

    private val logger = KotlinLogging.logger { }

    private val json = Json { ignoreUnknownKeys = true }

    private val baseHttpHeaders = mapOf(
        "Content-Type" to "application/json",
        "X-App-Version" to BuildConfig.APP_VERSION
    )

    fun build(
        deserializeRequestBody: (String?) -> RequestBody,
        deserializeQueryParameters: (Map<String, String>?) -> QueryParameters,
        serializeResponseBody: (ResponseBody) -> String?,
        block: (RequestBody, QueryParameters) -> ResponseBody
    ): (APIGatewayProxyRequestEvent) -> APIGatewayProxyResponseEvent = build@{ requestEvent ->
        withLoggingContext(
            "resource" to requestEvent.resource,
            "path" to requestEvent.path,
            "httpMethod" to requestEvent.httpMethod,
        ) {
            val requestBody = try {
                val body: String? = requestEvent.body
                deserializeRequestBody(body)
            } catch (e: Exception) {
                logger.catching(e)
                return@build createErrorResponse(
                    statusCode = 400,
                    errorMessage = "不正なリクエストボディです"
                )
            }

            val queryParameters = try {
                val parameters: Map<String, String>? = requestEvent.queryStringParameters
                deserializeQueryParameters(parameters)
            } catch (e: Exception) {
                logger.catching(e)
                return@build createErrorResponse(
                    statusCode = 400,
                    errorMessage = "不正なクエリパラメータです"
                )
            }

            try {
                val responseBody = block(requestBody, queryParameters)
                createResponse(
                    statusCode = 200,
                    body = serializeResponseBody(responseBody)
                )
            } catch (e: Exception) {
                logger.catching(e)
                return@build createErrorResponse()
            }
        }
    }

    private fun createErrorResponse(
        statusCode: Int = 500,
        errorMessage: String = "サーバエラーが発生しました"
    ) = createResponse(
        statusCode = statusCode,
        body = json.encodeToString(
            mapOf("ErrorMessage" to errorMessage)
        )
    )

    private fun createResponse(
        statusCode: Int = 200,
        body: String? = null
    ) = APIGatewayProxyResponseEvent().apply {
        this.statusCode = statusCode
        this.headers = baseHttpHeaders + httpHeaders
        this.body = body
    }
}