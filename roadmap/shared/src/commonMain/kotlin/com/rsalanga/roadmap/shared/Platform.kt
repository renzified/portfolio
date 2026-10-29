package com.rsalanga.roadmap.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
