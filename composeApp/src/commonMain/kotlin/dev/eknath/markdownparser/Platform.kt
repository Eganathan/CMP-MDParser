package dev.eknath.markdownparser

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform