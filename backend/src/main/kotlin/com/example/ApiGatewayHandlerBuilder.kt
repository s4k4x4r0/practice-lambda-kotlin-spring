package com.example

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.buildConfig.BuildConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ApiGatewayHandlerBuilder {

    inline fun <reified T, reified R> build(
        crossinline action: (T) -> R
    ) = handler@{ input: APIGatewayProxyRequestEvent ->
        withLoggingContext(
            "resource" to input.resource,
            "path" to input.path,
            "httpMethod" to input.httpMethod,
        ) {
            val logger = KotlinLogging.logger { }
            val json = Json { ignoreUnknownKeys = true }

            val responseEvent = APIGatewayProxyResponseEvent()
                .withHeaders(
                    mapOf(
                        "X-App-Version" to BuildConfig.APP_VERSION
                    )
                )

            val requestBody = try {
                if (T::class != Unit::class) {
                    json.decodeFromString<T>(input.body)
                } else {
                    Unit as T
                }
            } catch (e: Exception) {
                logger.catching(e)
                return@handler responseEvent
                    .withStatusCode(400)
                    .withBody(
                        json.encodeToString(
                            mapOf("ErrorMessage" to "不正なリクエストボディです")
                        )
                    )
            }
            try {
                val responseBody = action(requestBody)
                return@handler if (R::class != Unit::class) {
                    val responseBodyJson = json.encodeToString(responseBody)
                    responseEvent
                        .withStatusCode(200)
                        .withBody(responseBodyJson)
                } else {
                    responseEvent
                        .withStatusCode(200)
                }
            } catch (e: Exception) {
                logger.catching(e)
                return@handler responseEvent
                    .withStatusCode(500)
                    .withBody(
                        json.encodeToString(
                            mapOf("ErrorMessage" to "サーバエラーが発生しました")
                        )
                    )
            }
        }
    }
}