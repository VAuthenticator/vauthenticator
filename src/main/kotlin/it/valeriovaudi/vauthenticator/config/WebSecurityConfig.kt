package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain


const val adminRole = "VAUTHENTICATOR_ADMIN"

private const val LOG_IN_URL_PAGE = "/login"
private val WHITE_LIST = arrayOf(
        "/logout",
        "/oidc/logout",
        "/login",
        "/webjars/**",
        "/api/**"
)

@Configuration(proxyBeanMethods = false)
class WebSecurityConfig {

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity,
                                   accountUserDetailsService: AccountUserDetailsService): SecurityFilterChain {
        http.csrf().disable()
                .formLogin()
                .loginProcessingUrl("/login")
                .loginPage(LOG_IN_URL_PAGE)
                .permitAll()

        http.logout().invalidateHttpSession(true)

        http.requestMatchers().antMatchers(*WHITE_LIST)
                .and()
                .authorizeRequests()
                .mvcMatchers("/api/accounts")
                .permitAll()
                .and()

                .authorizeRequests()
                .mvcMatchers("/api/**")
                .hasAnyAuthority(adminRole)
                .and()

        http.userDetailsService(accountUserDetailsService)
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter())

        return http.build()
    }

    fun jwtAuthenticationConverter(): JwtAuthenticationConverter? {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter { jwt: Jwt ->
            val authoritiesClaims = jwt.getClaim<List<String>>("authorities")
            authoritiesClaims.map { role: String -> SimpleGrantedAuthority(role) }
        }
        return jwtAuthenticationConverter
    }
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(12)
    }

    @Bean
    fun accountUserDetailsService(userRepository: AccountRepository) =
            AccountUserDetailsService(userRepository)
}