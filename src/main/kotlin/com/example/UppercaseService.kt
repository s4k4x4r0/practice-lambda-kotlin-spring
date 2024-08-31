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
            .apply {
                if (lengthLimit != null && length > lengthLimit) {
                    substring(0, lengthLimit)
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
