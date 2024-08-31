package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class UppercaseResponse(
    val uppercase: String,
    val original: String,
    val length: Int
)
