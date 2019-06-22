package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.time.SystemUTCClock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TimeConfig {

    @Bean
    fun clock() = SystemUTCClock()
}