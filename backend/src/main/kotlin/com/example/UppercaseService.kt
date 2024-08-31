package com.example

import org.springframework.stereotype.Service

@Service
class UppercaseService {
    fun convertToUppercase(
        input: String,
        lengthLimit: Int? = null,
        applyPrefix: Boolean = false,
        prefix: String? = null
    ): String {
        return input
            .uppercase()
            .let {
                if (lengthLimit != null && it.length > lengthLimit) {
                    it.substring(0, lengthLimit)
                } else {
                    it
                }
            }.let {
                if (applyPrefix && prefix != null) {
                    prefix + it
                } else {
                    it
                }
            }
    }
}
