package com.example

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.example.models.UppercaseRequest
import com.example.models.UppercaseResponse
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UppercaseConfig {

    private val json = Json { ignoreUnknownKeys = true }

    @Bean
    fun uppercaseFunction(
        uppercaseService: UppercaseService
    ) = { input: APIGatewayProxyRequestEvent ->
        val body = input.body
        val request = json.decodeFromString<UppercaseRequest>(body)

        val result = uppercaseService.convertToUppercase(
            input = request.input,
            lengthLimit = request.lengthLimit,
            applyPrefix = request.applyPrefix == true,
            prefix = request.prefix
        )

        val response = UppercaseResponse(
            uppercase = result,
            original = request.input,
            length = result.length
        )

        APIGatewayProxyResponseEvent()
            .withStatusCode(200)
            .withBody(json.encodeToString(response))
    }
}