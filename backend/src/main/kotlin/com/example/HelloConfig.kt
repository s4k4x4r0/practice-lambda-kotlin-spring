package com.example

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.models.HelloResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.withLoggingContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HelloConfig {

    private val logger = KotlinLogging.logger { }

    @Bean
    fun helloFunction(
        helloService: HelloService
    ): (APIGatewayProxyRequestEvent) -> APIGatewayProxyResponseEvent = { requestEvent ->
        withLoggingContext(
            "resource" to requestEvent.resource,
            "path" to requestEvent.path,
            "httpMethod" to requestEvent.httpMethod,
        ) {
            try {
                logger.debug { "start helloFunction" }
                val helloMessage = helloService.getHelloMessage()

                val response = HelloResponse(value = helloMessage)

                APIGatewayProxyResponseEvent().withStatusCode(200)
                    .withBody(Json.encodeToString(response))
            } catch (e: Exception) {
                logger.catching(e)
                APIGatewayProxyResponseEvent().withStatusCode(500)
            }
        }
    }
}