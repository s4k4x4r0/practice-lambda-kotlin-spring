package com.example

import com.example.models.UppercaseRequest
import com.example.models.UppercaseResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UppercaseConfig {

    @Bean
    fun uppercaseFunction(
        uppercaseService: UppercaseService
    ) = ApiGatewayHandlerBuilder.build<UppercaseRequest, UppercaseResponse> { request ->
        val result = uppercaseService.convertToUppercase(
            input = request.input,
            lengthLimit = request.lengthLimit,
            applyPrefix = request.applyPrefix == true,
            prefix = request.prefix
        )
        UppercaseResponse(
            uppercase = result,
            original = request.input,
            length = result.length
        )
    }
}