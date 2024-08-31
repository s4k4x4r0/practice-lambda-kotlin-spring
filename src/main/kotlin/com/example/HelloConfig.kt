package com.example

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Function

@Configuration
class HelloConfig {

    @Bean
    fun helloFunction(helloService: HelloService): Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
        return Function {
            val response = APIGatewayProxyResponseEvent()
            response.withStatusCode(200)
                .withBody(helloService.getHelloMessage())
            response
        }
    }
}
