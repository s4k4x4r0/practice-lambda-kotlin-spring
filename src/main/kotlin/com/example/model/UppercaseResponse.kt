package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class UppercaseResponse(
    val uppercase: String,
    val original: String,
    val length: Int
)
