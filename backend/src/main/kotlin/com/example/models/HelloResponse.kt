package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class HelloResponse(
    val value: String
)