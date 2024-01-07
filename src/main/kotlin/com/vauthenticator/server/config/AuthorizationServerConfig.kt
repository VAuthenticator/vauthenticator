package com.vauthenticator.server.config

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.keys.KeyDecrypter
import com.vauthenticator.server.keys.KeyRepository
import com.vauthenticator.server.keys.KeysJWKSource
import com.vauthenticator.server.keys.Kid
import com.vauthenticator.server.oauth2.authorizationservice.RedisOAuth2AuthorizationService
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.Scope.Companion.AVAILABLE_SCOPES
import com.vauthenticator.server.oauth2.clientapp.Scope.Companion.OPEN_ID
import com.vauthenticator.server.oauth2.clientapp.StoreClientApplication
import com.vauthenticator.server.oauth2.registeredclient.ClientAppRegisteredClientRepository
import com.vauthenticator.server.oauth2.token.OAuth2TokenEnhancer
import com.vauthenticator.server.oidc.sessionmanagement.SessionManagementFactory
import com.vauthenticator.server.oidc.sessionmanagement.sendAuthorizationResponse
import com.vauthenticator.server.oidc.token.IdTokenEnhancer
import com.vauthenticator.server.oidc.userinfo.UserInfoEnhancer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint

@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {

    @Value("\${auth.oidcIss:}")
    lateinit var oidcIss: String

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Bean
    fun jwkSource(keyRepository: KeyRepository, keyDecrypter: KeyDecrypter): JWKSource<SecurityContext?> =
        KeysJWKSource(keyDecrypter, keyRepository)

    @Bean
    fun nimbusJwsEncoder(jwkSource: JWKSource<SecurityContext?>?): JwtEncoder {
        return NimbusJwtEncoder(jwkSource)
    }
    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext?>?): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }
    @Bean
    fun jwtCustomizer(
        keyRepository: KeyRepository,
        clientApplicationRepository: ClientApplicationRepository): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context: JwtEncodingContext ->
            val assignedKeys = mutableSetOf<Kid>()
            OAuth2TokenEnhancer(assignedKeys, keyRepository, clientApplicationRepository).customize(context)
            IdTokenEnhancer(assignedKeys, keyRepository).customize(context)
        }
    }

    @Bean
    fun registeredClientRepository(
        storeClientApplication: StoreClientApplication,
        clientRepository: ClientApplicationRepository
    ): RegisteredClientRepository {
        return ClientAppRegisteredClientRepository(storeClientApplication, clientRepository)
    }

    @Bean
    fun oAuth2AuthorizationService(redisTemplate: RedisTemplate<Any, Any>): OAuth2AuthorizationService {
        return RedisOAuth2AuthorizationService(redisTemplate)
    }

    @Bean
    fun providerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder().issuer(oidcIss).build()
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(
        providerSettings: AuthorizationServerSettings,
        redisTemplate: RedisTemplate<String, String?>,
        http: HttpSecurity
    ): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http.csrf { it.disable() }.headers { it.frameOptions { it.disable() } }

        val userInfoEnhancer = UserInfoEnhancer(accountRepository)

        val authorizationServerConfigurer = http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)

        authorizationServerConfigurer.oidc { configurer ->
            configurer.userInfoEndpoint { customizer ->
                customizer.userInfoMapper { context ->
                    userInfoEnhancer.oidcUserInfoFrom(context)
                }
            }.providerConfigurationEndpoint { customizer ->
                customizer.providerConfigurationCustomizer { providerConfiguration ->
                    AVAILABLE_SCOPES
                        .filter { it != OPEN_ID }
                        .forEach { providerConfiguration.scope(it.content) }
                }
            }
        }.authorizationEndpoint {
            it.authorizationResponseHandler(
                sendAuthorizationResponse(
                    redisTemplate,
                    SessionManagementFactory(providerSettings),
                    DefaultRedirectStrategy()
                )
            )
        }
        http.exceptionHandling { it.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login")) }
            .oauth2ResourceServer { it.jwt {} }

        return http.build()
    }

}

