package com.example

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.models.HelloResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Function

@Configuration
class HelloConfig {

    @Bean
    fun helloFunction(helloService: HelloService): Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
        return Function { request ->
            val helloMessage = helloService.getHelloMessage()
            val helloResponse = HelloResponse(value = helloMessage)

            val response = APIGatewayProxyResponseEvent()
            val jsonResponse = Json.encodeToString(helloResponse)
            response.withStatusCode(200)
                .withBody(jsonResponse)
            response
        }
    }
}