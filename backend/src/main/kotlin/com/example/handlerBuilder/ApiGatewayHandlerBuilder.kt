package com.example.handlerBuilder

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.buildConfig.BuildConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ApiGatewayHandlerBuilder {

    private val logger = KotlinLogging.logger { }

    private val json = Json { ignoreUnknownKeys = true }

    internal inline fun <reified T, reified Q, reified R> build(
        crossinline action: (T, Q) -> R
    ) = handler@{ input: APIGatewayProxyRequestEvent ->
        withLoggingContext(
            "resource" to input.resource,
            "path" to input.path,
            "httpMethod" to input.httpMethod,
        ) {
            val requestBody = try {
                parseRequestBody<T>(input.body as String?)
            } catch (e: Exception) {
                logger.catching(e)
                return@handler createErrorResponse(
                    statusCode = 400,
                    errorMessage = "不正なリクエストボディです"
                )
            }

            val queryParameters = try {
                parseQueryParameters<Q>(input.queryStringParameters as Map<String, String>?)
            } catch (e: Exception) {
                logger.catching(e)
                return@handler createErrorResponse(
                    statusCode = 400,
                    errorMessage = "不正なクエリパラメータです"
                )
            }

            try {
                val responseBodyJson = executeAction(
                    action = action,
                    requestBody = requestBody,
                    queryParameters = queryParameters
                )
                return@handler createResponse(
                    body = responseBodyJson
                )
            } catch (e: Exception) {
                logger.catching(e)
                return@handler createErrorResponse()
            }
        }
    }

    private inline fun <reified T> parseRequestBody(body: String?) =
        if (T::class == Unit::class) Unit as T
        else json.decodeFromString<T>(body ?: "")


    private inline fun <reified Q> parseQueryParameters(queryParameters: Map<String, String>?) =
        if (Q::class == Unit::class) Unit as Q
        else json.decodeFromString<Q>(
            json.encodeToString(queryParameters ?: mapOf("" to ""))
        )

    private inline fun <reified T, reified Q, reified R> executeAction(
        action: (T, Q) -> R,
        requestBody: T,
        queryParameters: Q
    ): String? {
        val responseBody = action(requestBody, queryParameters)
        return if (R::class != Unit::class) {
            json.encodeToString(responseBody)
        } else null
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

    internal fun createResponse(
        statusCode: Int = 200,
        headers: Map<String, String> = mapOf(
            "X-App-Version" to BuildConfig.APP_VERSION
        ),
        body: String? = null
    ) = APIGatewayProxyResponseEvent()
        .withStatusCode(statusCode)
        .withHeaders(headers)
        .apply {
            body?.let { withBody(it) }
        }

}