package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class UppercaseRequest(
    val input: String,
    val lengthLimit: Int? = null,
    val applyPrefix: Boolean? = null,
    val prefix: String? = null
)
