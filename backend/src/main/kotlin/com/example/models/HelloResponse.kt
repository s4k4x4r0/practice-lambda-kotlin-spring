//package com.example
//
//import kotlinx.serialization.Serializable
//
//@Serializable
//data class HelloResponse(val value: String)

/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package com.example.models


import kotlinx.serialization.SerialName
import java.io.Serializable
import kotlinx.serialization.Serializable as KSerializable

/**
 *
 *
 * @param `value`
 */
@KSerializable

data class HelloResponse(

    @SerialName(value = "value")
    val `value`: kotlin.String? = null

) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 123
    }


}

