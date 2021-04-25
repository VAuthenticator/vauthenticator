package it.valeriovaudi.vauthenticator.config

import com.nimbusds.jose.jwk.JWKSelector
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import it.valeriovaudi.vauthenticator.account.AccountRepository
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.openid.connect.logout.JdbcFrontChannelLogout
import it.valeriovaudi.vauthenticator.security.registeredclient.ClientAppRegisteredClientRepository
import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import it.valeriovaudi.vauthenticator.time.Clock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.NimbusJwsEncoder
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.web.SecurityFilterChain
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import java.util.stream.Collectors
import javax.sql.DataSource

@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {

    @Value("\${auth.oidcIss:}")
    lateinit var oidcIss: String

    @Value("\${key-store.keyStorePairAlias:}")
    lateinit var alias: String

    @Autowired
    lateinit var keyRepository: KeyRepository

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var clock: Clock

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        return http.build()
    }

    fun generateRsa(): RSAKey {
        val keyPair = keyRepository.getKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        return RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
    }


    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val rsaKey = generateRsa()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource { jwkSelector: JWKSelector, securityContext: SecurityContext ->
            jwkSelector.select(
                jwkSet
            )
        }
    }

    @Bean
    fun nimbusJwsEncoder(jwkSource: JWKSource<SecurityContext>): NimbusJwsEncoder {
        return NimbusJwsEncoder(jwkSource)
    }

    //    @Bean
    fun jwtCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context: JwtEncodingContext ->
            val tokenType = context.tokenType.value
            println(tokenType)
            if ("access_token" == tokenType) {
                val attributes =
                    context.authorization!!.attributes
                val principle =
                    attributes["java.security.Principal"] as Authentication
                context.claims.claim("role", principle.authorities
                    .stream()
                    .map { obj: GrantedAuthority -> obj.authority }
                    .collect(Collectors.toList()))
            }
        }
    }


    @Bean
    fun registeredClientRepository(
        clientRepository: ClientApplicationRepository,
        passwordEncoder: PasswordEncoder
    ): RegisteredClientRepository {
        return ClientAppRegisteredClientRepository(clientRepository)
    }

    @Bean
    fun oAuth2AuthorizationService(): OAuth2AuthorizationService {
        return InMemoryOAuth2AuthorizationService()
    }

    @Bean
    fun providerSettings(): ProviderSettings {
        return ProviderSettings().issuer(oidcIss)
    }

    @Bean
    fun frontChannelLogout(dataSource: DataSource) = JdbcFrontChannelLogout(oidcIss, JdbcTemplate(dataSource))
}