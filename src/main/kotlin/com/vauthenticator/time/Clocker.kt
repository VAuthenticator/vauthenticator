package com.vauthenticator.time

import java.time.Clock
import java.time.Instant

interface Clocker {
    fun now() : Instant
}

class UtcClocker() : Clocker {
    override fun now(): Instant {
        val systemUTC = Clock.systemUTC()
        return Instant.now(systemUTC)

    }

}