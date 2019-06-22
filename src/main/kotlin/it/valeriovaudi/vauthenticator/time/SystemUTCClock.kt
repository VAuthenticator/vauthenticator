package it.valeriovaudi.vauthenticator.time

import java.time.Clock.systemUTC

interface Clock {
    fun nowInSeconds(): Long
}

class SystemUTCClock : Clock {

    override fun nowInSeconds() = systemUTC().instant().toEpochMilli() / 1000;

}