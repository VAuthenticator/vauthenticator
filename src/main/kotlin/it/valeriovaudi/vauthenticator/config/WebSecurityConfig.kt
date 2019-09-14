package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.oauth2.codeservice.RedisAuthorizationCodeServices
import it.valeriovaudi.vauthenticator.openid.connect.nonce.InMemoryNonceStore
import it.valeriovaudi.vauthenticator.openid.connect.nonce.NonceStore
import it.valeriovaudi.vauthenticator.userdetails.AccountUserDetailsService
import it.valeriovaudi.vauthenticator.userdetails.LogInRequestGateway
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.OAuth2Authentication
import java.util.concurrent.ConcurrentHashMap

@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    private val LOG_IN_URL_PAGE = "/singin"
    private val WHITE_LIST = arrayOf("/logout", "/oidc/logout", "/singin", "/user-info", "/oauth/authorize", "/oauth/confirm_access", "/webjars/**")

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .formLogin().loginPage(LOG_IN_URL_PAGE)
                .loginProcessingUrl(LOG_IN_URL_PAGE)
                .permitAll()
                .and()
                .logout()
                .and()
                .requestMatchers().antMatchers(*WHITE_LIST)
                .and()
                .authorizeRequests().anyRequest().permitAll()
                .and().oauth2ResourceServer().jwt()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    fun redisAuthorizationCodeServices(redisTemplate: RedisTemplate<*, *>, nonceStore: NonceStore) =
            RedisAuthorizationCodeServices(redisTemplate as RedisTemplate<String, OAuth2Authentication>, nonceStore)

    @Bean
    fun accountUserDetailsService(logInRequestGateway: LogInRequestGateway) =
            AccountUserDetailsService(logInRequestGateway)


    @Bean
    fun nonceStore() = InMemoryNonceStore(ConcurrentHashMap())

}