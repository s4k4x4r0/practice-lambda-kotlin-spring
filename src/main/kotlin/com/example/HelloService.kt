package com.example

import org.springframework.stereotype.Service

@Service
class HelloService {
    fun getHelloMessage(): String {
        return "Hello, World!"
    }
}
