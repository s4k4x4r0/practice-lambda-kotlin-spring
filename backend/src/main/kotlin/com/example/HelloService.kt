package com.example

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class HelloService {

    private val logger = KotlinLogging.logger { }

    fun getHelloMessage(): String {
        logger.info { "returning hello world" }
        return "Hello, World!"
    }
}
