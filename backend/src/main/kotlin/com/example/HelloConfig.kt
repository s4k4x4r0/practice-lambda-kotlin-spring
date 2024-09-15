package com.example

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.models.HelloResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HelloConfig {

    @Bean
    fun helloFunction(
        helloService: HelloService
    ) : (APIGatewayProxyRequestEvent) -> APIGatewayProxyResponseEvent = {
        val helloMessage = helloService.getHelloMessage()
        val response = HelloResponse(value = helloMessage)

        APIGatewayProxyResponseEvent().withStatusCode(200)
            .withBody(Json.encodeToString(response))
    }
}