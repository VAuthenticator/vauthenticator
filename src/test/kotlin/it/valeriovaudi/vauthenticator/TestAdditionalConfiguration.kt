package it.valeriovaudi.vauthenticator

import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.KeyPair

@Configuration
class TestAdditionalConfiguration {

    @Bean
    fun keyRepository() = object : KeyRepository {
        override fun getKeyPair() = KeyPair(null, null)

    }
}