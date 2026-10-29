package com.rsalanga.roadmap.shared

import kotlin.test.Test
import kotlin.test.assertEquals

class GreetingTest {
    @Test
    fun greet_includesPlatformName() {
        val platform = object : Platform {
            override val name = "Test"
        }
        assertEquals("Hello, Test!", Greeting(platform).greet())
    }
}
