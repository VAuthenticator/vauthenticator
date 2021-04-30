package it.valeriovaudi.vauthenticator

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean


@EnableCaching
@SpringBootApplication
class VAuthenticatorApplication {
    @Bean
    fun configurer(@Value("\${spring.application.name:}") applicationName: String): MeterRegistryCustomizer<MeterRegistry>? {
        return MeterRegistryCustomizer { registry: MeterRegistry -> registry.config().commonTags("application", applicationName) }
    }

}

fun main(args: Array<String>) {
    runApplication<VAuthenticatorApplication>(*args)
}
