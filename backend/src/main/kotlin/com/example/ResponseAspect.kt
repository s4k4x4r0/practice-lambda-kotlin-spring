package com.example

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import com.example.backend.BuildConfig
import org.springframework.stereotype.Component

@Aspect
@Component
class ResponseAspect {

    @AfterReturning(
        pointcut = "bean(*Function)",
        returning = "response"
    )
    fun addVersionHeader(response: Any) {
        if(response is APIGatewayProxyResponseEvent) {
            val headers = response.headers?.toMutableMap() ?: mutableMapOf()
            headers["X-App-Version"] = BuildConfig.APP_VERSION
            response.headers = headers
        }
    }
}