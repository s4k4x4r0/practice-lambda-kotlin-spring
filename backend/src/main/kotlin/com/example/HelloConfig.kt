package com.example

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.models.HelloResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HelloConfig {
    @Bean
    fun helloFunction(
        helloService: HelloService
    ): (APIGatewayProxyRequestEvent) -> APIGatewayProxyResponseEvent =
        ApiGatewayHandlerBuilder.build<Unit, HelloResponse> {
            val helloMessage = helloService.getHelloMessage()
            HelloResponse(value = helloMessage)
        }
}