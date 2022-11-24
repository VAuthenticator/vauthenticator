package it.valeriovaudi.vauthenticator.config

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.keypair.KeysJWKSource
import it.valeriovaudi.vauthenticator.oauth2.authorizationservice.RedisOAuth2AuthorizationService
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope
import it.valeriovaudi.vauthenticator.oauth2.clientapp.StoreClientApplication
import it.valeriovaudi.vauthenticator.oauth2.token.OAuth2TokenEnhancer
import it.valeriovaudi.vauthenticator.openid.connect.sessionmanagement.SessionManagementFactory
import it.valeriovaudi.vauthenticator.openid.connect.sessionmanagement.sendAuthorizationResponse
import it.valeriovaudi.vauthenticator.openid.connect.token.IdTokenEnhancer
import it.valeriovaudi.vauthenticator.openid.connect.userinfo.UserInfoEnhancer
import it.valeriovaudi.vauthenticator.security.registeredclient.ClientAppRegisteredClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {

    @Value("\${auth.oidcIss:}")
    lateinit var oidcIss: String

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
    fun providerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder().issuer(oidcIss).build()
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(
        introspector: HandlerMappingIntrospector,
        providerSettings: AuthorizationServerSettings,
        redisTemplate: RedisTemplate<String, String?>,
        http: HttpSecurity
    ): SecurityFilterChain {
        http.authorizeHttpRequests().requestMatchers("/actuator/**", "/session/**", "/check_session").permitAll()
        http.authorizeHttpRequests().requestMatchers(
            MvcRequestMatcher.Builder(introspector).servletPath("/").pattern("/api/sign-up/mail/{mail}/welcome")
        ).hasAnyAuthority(Scope.WELCOME.content)


        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http.csrf().disable().headers().frameOptions().disable()

        val userInfoEnhancer = UserInfoEnhancer(accountRepository)

        val authorizationServerConfigurer = http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)

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
        http.exceptionHandling { it.authenticationEntryPoint(LoginUrlAuthenticationEntryPoint("/login")) }
            .oauth2ResourceServer().jwt()

        return http.build()
    }


    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext?>?): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

}

