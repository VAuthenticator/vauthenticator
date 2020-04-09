package it.valeriovaudi.vauthenticator

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.config.EnableIntegration


@EnableCaching
@EnableIntegration
@SpringBootApplication
@IntegrationComponentScan
class VauthenticatorApplication {
    @Bean
    fun configurer(@Value("\${spring.application.name:}") applicationName: String): MeterRegistryCustomizer<MeterRegistry>? {
        return MeterRegistryCustomizer { registry: MeterRegistry -> registry.config().commonTags("application", applicationName) }
    }

}

fun main(args: Array<String>) {
    runApplication<VauthenticatorApplication>(*args)
}
