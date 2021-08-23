package it.valeriovaudi.vauthenticator.config

import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import it.valeriovaudi.vauthenticator.account.AccountRepository
import it.valeriovaudi.vauthenticator.extentions.BcryptVAuthenticatorPasswordEncoder
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.oauth2.authorizationservice.RedisOAuth2AuthorizationService
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.openid.connect.logout.JdbcFrontChannelLogout
import it.valeriovaudi.vauthenticator.security.registeredclient.ClientAppRegisteredClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwsEncoder
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.web.SecurityFilterChain
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.stream.Collectors

@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {

    @Value("\${auth.oidcIss:}")
    lateinit var oidcIss: String

    @Autowired
    lateinit var keyRepository: KeyRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    fun generateRsa(): RSAKey {
        val keyPair = this.keyRepository.getKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        return RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("onlyone-portal-key")
                .build()
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext?> {
        val rsaKey = generateRsa()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector: JWKSelector, _: SecurityContext? ->
            jwkSelector.select(
                    jwkSet
            )
        }
    }

    @Bean
    fun nimbusJwsEncoder(jwkSource: JWKSource<SecurityContext?>?): NimbusJwsEncoder {
        return NimbusJwsEncoder(jwkSource)
    }

    @Bean
    fun jwtCustomizer(clientApplicationRepository: ClientApplicationRepository): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context: JwtEncodingContext ->
            val tokenType = context.tokenType.value
            if ("access_token" == tokenType && !context.authorizationGrantType.equals(CLIENT_CREDENTIALS)) {
                val attributes =
                        context.authorization!!.attributes
                val principal =
                        attributes["java.security.Principal"] as Authentication

                context.claims.claim("user_name", principal.name)
                context.claims.claim("authorities", principal.authorities
                        .stream()
                        .map { obj: GrantedAuthority -> obj.authority }
                        .collect(Collectors.toList()))
            }

            if ("id_token" == tokenType && !context.authorizationGrantType.equals(CLIENT_CREDENTIALS)) {
                val attributes =
                        context.authorization!!.attributes
                val principle =
                        attributes["java.security.Principal"] as Authentication
                clientApplicationRepository.findOne(
                        ClientAppId(context.registeredClient.clientId)
                ).ifPresent {
                    context.claims.claim("federation", it.federation.name)
                }

                context.claims.claim("email", principle.name)
            }
        }
    }

    @Bean
    fun registeredClientRepository(clientRepository: ClientApplicationRepository): RegisteredClientRepository {
        return ClientAppRegisteredClientRepository(clientRepository)
    }

    @Bean
    fun oAuth2AuthorizationService(redisTemplate: RedisTemplate<Any, Any>): OAuth2AuthorizationService {
//        return InMemoryOAuth2AuthorizationService()
        return RedisOAuth2AuthorizationService(redisTemplate)
    }

    @Bean
    fun providerSettings(): ProviderSettings {
        return ProviderSettings.builder().issuer(oidcIss).build()
    }

    @Bean
    fun frontChannelLogout(applicationRepository: ClientApplicationRepository) =
            JdbcFrontChannelLogout(
                    oidcIss, applicationRepository
            )


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        return http.formLogin(Customizer.withDefaults()).build()
    }


    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext?>?): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun bcryptAccountPasswordEncoder(passwordEncoder: PasswordEncoder) =
            BcryptVAuthenticatorPasswordEncoder(passwordEncoder)
}

