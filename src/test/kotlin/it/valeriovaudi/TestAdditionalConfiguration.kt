package it.valeriovaudi

import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import java.security.KeyPair

@Configuration
class TestAdditionalConfiguration {

    @Bean
    fun keyRepository() = object : KeyRepository {
        override fun getKeyPair() = KeyPair(null, null)
    }

    @Bean
    fun jwtDecoder() = Mockito.mock(JwtDecoder::class.java)
}