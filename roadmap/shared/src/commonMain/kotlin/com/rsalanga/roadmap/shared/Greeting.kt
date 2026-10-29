package com.rsalanga.roadmap.shared

class Greeting(private val platform: Platform) {
    // Default arguments aren't exported to Swift, so expose a no-arg constructor explicitly.
    constructor() : this(getPlatform())

    fun greet(): String = "Hello, ${platform.name}!"
}
