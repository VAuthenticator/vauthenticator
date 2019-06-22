package it.valeriovaudi.vauthenticator.time

import java.time.Clock.systemUTC

open class Clock {

    open fun nowInSeconds() = systemUTC().instant().toEpochMilli() / 1000;
}
