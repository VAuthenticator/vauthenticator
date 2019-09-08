package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.codeservice.RedisAuthorizationCodeServices
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.openid.connect.idtoken.IdTokenEnhancer
import it.valeriovaudi.vauthenticator.time.Clock
import it.valeriovaudi.vauthenticator.userdetails.AccountUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import javax.sql.DataSource


@Configuration
@EnableAuthorizationServer
class SecurityOAuth2AutorizationServerConfig(private val accountUserDetailsService: AccountUserDetailsService,
                                             private val authenticationManager: AuthenticationManager,
                                             private val passwordEncoder: PasswordEncoder) : AuthorizationServerConfigurerAdapter() {

    @Value("\${auth.oidcIss:}")
    lateinit var oidcIss: String

    @Autowired
    lateinit var keyRepository: KeyRepository


    @Autowired
    lateinit var redisAuthorizationCodeServices: RedisAuthorizationCodeServices

    @Autowired
    lateinit var redisConnectionFactory: RedisConnectionFactory

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var clock: Clock

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        val tokenEnhancerChain = TokenEnhancerChain()
        tokenEnhancerChain.setTokenEnhancers(mutableListOf(accessTokenConverter(), IdTokenEnhancer(oidcIss, keyRepository, clock)))

        endpoints.approvalStoreDisabled()
                .authorizationCodeServices(redisAuthorizationCodeServices)
                .authenticationManager(authenticationManager)
                .tokenEnhancer(tokenEnhancerChain)
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .userDetailsService(accountUserDetailsService)
                .reuseRefreshTokens(false)
    }


    override fun configure(oauthServer: AuthorizationServerSecurityConfigurer) {
        oauthServer.tokenKeyAccess("permitAll()")
                .passwordEncoder(passwordEncoder)
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients()
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.jdbc(dataSource)
                .passwordEncoder(passwordEncoder)
    }

    @Bean
    fun tokenStore() = JwtTokenStore(accessTokenConverter())

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val keyPair = keyRepository.getKeyPair()
        val jwtAccessTokenConverter = JwtAccessTokenConverter()
        jwtAccessTokenConverter.setKeyPair(keyPair)
        return jwtAccessTokenConverter
    }

}