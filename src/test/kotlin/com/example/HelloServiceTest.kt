package com.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class HelloServiceTest {

    @Autowired
    private lateinit var helloService: HelloService

    @Test
    fun `getHelloMessage should return Hello, World!`() {
        val result = helloService.getHelloMessage()
        assertEquals("Hello, World!", result)
    }
}