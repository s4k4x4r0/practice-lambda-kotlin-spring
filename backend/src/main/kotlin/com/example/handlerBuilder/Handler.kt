package com.example.handlerBuilder

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Handler {
    private val json = Json { ignoreUnknownKeys = true }

    private inline fun <reified T> getRequestBodyDeserializer(): (String?) -> T = {
        requireNotNull(it?.let { json.decodeFromString(it) })
    }

    private inline fun <reified Q> getQueryParametersDeserializer(): (Map<String, String>?) -> Q = {
        requireNotNull(it?.let {
            json.decodeFromString(
                json.encodeToString(it)
            )
        })
    }

    private inline fun <reified R> getResponseBodySerializer(): (R) -> String? = {
        if (it == Unit || it == null) null
        else json.encodeToString((it))
    }

    private val IGNORE_INPUT: (Any?) -> Unit = {}

    internal inline fun <reified T, reified R> createApiHandler(
        noinline block: (T) -> R
    ) = ApiGatewayHandlerBuilder<T, Unit, R>().build(
        deserializeRequestBody = getRequestBodyDeserializer(),
        deserializeQueryParameters = IGNORE_INPUT,
        serializeResponseBody = getResponseBodySerializer(),
        block = { requestBody, _ -> block(requestBody) }
    )


    internal inline fun <reified R> createApiHandlerWithoutBody(
        noinline block: () -> R
    ) = ApiGatewayHandlerBuilder<Unit, Unit, R>().build(
        deserializeRequestBody = IGNORE_INPUT,
        deserializeQueryParameters = IGNORE_INPUT,
        serializeResponseBody = getResponseBodySerializer(),
        block = { _, _ -> block() }
    )


    internal inline fun <reified Q, reified R> createApiHandlerWithoutBodyWithQueryParameter(
        noinline block: (Q) -> R
    ) = ApiGatewayHandlerBuilder<Unit, Q, R>().build(
        deserializeRequestBody = IGNORE_INPUT,
        deserializeQueryParameters = getQueryParametersDeserializer(),
        serializeResponseBody = getResponseBodySerializer(),
        block = { _, queryParameters -> block(queryParameters) }
    )


    internal inline fun <reified T, reified Q, reified R> createApiHandlerWithQueryParameter(
        noinline block: (T, Q) -> R
    ) = ApiGatewayHandlerBuilder<T, Q, R>().build(
        deserializeRequestBody = getRequestBodyDeserializer(),
        deserializeQueryParameters = getQueryParametersDeserializer(),
        serializeResponseBody = getResponseBodySerializer(),
        block = block
    )

}