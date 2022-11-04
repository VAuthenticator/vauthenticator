package it.valeriovaudi.vauthenticator.config

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.keypair.KeysJWKSource
import it.valeriovaudi.vauthenticator.oauth2.authorizationservice.RedisOAuth2AuthorizationService
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.StoreClientApplication
import it.valeriovaudi.vauthenticator.oauth2.token.OAuth2TokenEnhancer
import it.valeriovaudi.vauthenticator.openid.connect.sessionmanagement.SessionManagementFactory
import it.valeriovaudi.vauthenticator.openid.connect.sessionmanagement.sendAuthorizationResponse
import it.valeriovaudi.vauthenticator.openid.connect.token.IdTokenEnhancer
import it.valeriovaudi.vauthenticator.openid.connect.userinfo.UserInfoEnhancer
import it.valeriovaudi.vauthenticator.password.BcryptVAuthenticatorPasswordEncoder
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
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.SecurityFilterChain

@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {

    @Value("\${auth.oidcIss:}")
    lateinit var oidcIss: String

    @Autowired
    lateinit var keyRepository: KeyRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Bean
    fun jwkSource(keyRepository: KeyRepository): JWKSource<SecurityContext?> =
        KeysJWKSource(keyRepository)

    @Bean
    fun nimbusJwsEncoder(jwkSource: JWKSource<SecurityContext?>?): JwtEncoder {
        return NimbusJwtEncoder(jwkSource)
    }

    @Bean
    fun jwtCustomizer(clientApplicationRepository: ClientApplicationRepository): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context: JwtEncodingContext ->
            OAuth2TokenEnhancer(clientApplicationRepository).customize(context)
            IdTokenEnhancer().customize(context)
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
    fun providerSettings(): ProviderSettings {
        return ProviderSettings.builder().issuer(oidcIss).build()
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(
        providerSettings: ProviderSettings,
        redisTemplate: RedisTemplate<String, String?>,
        http: HttpSecurity
    ): SecurityFilterChain {
        val userInfoEnhancer = UserInfoEnhancer(accountRepository)
        val authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer<HttpSecurity>()
        authorizationServerConfigurer.oidc { configurer ->
            configurer.userInfoEndpoint { customizer ->
                customizer.userInfoMapper { context ->
                    userInfoEnhancer.oidcUserInfoFrom(context)
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
        val endpointsMatcher = authorizationServerConfigurer.endpointsMatcher

        http
            .requestMatcher(endpointsMatcher)
            .authorizeRequests { authorizeRequests -> authorizeRequests.anyRequest().authenticated() }
            .csrf { csrf: CsrfConfigurer<HttpSecurity?> -> csrf.ignoringRequestMatchers(endpointsMatcher) }
            .apply(authorizationServerConfigurer)

        return http.formLogin(Customizer.withDefaults())
            .oauth2ResourceServer().jwt()
            .and().and()
            .build()
    }


    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext?>?): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun bcryptAccountPasswordEncoder(passwordEncoder: PasswordEncoder) =
        BcryptVAuthenticatorPasswordEncoder(passwordEncoder)
}

