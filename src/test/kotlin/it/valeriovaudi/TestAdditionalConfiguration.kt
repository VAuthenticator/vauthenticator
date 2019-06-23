package it.valeriovaudi

import it.valeriovaudi.vauthenticator.keypair.FileKeyRepository
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.oauth2.jwt.JwtDecoder

@Configuration
class TestAdditionalConfiguration {

    @Bean
    fun keyRepository() = FileKeyRepository(KeyPairFixture.happyPathKeyPairConfig())

    @Bean
    fun jwtDecoder() = Mockito.mock(JwtDecoder::class.java)

    @Bean
    @Profile("web-tier")
    fun redisTemplate() = Mockito.mock(RedisTemplate::class.java)
}