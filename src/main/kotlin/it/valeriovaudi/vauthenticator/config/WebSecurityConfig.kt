package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope
import it.valeriovaudi.vauthenticator.openid.connect.logout.ClearSessionStateLogoutHandler
import it.valeriovaudi.vauthenticator.openid.connect.sessionmanagement.SessionManagementFactory
import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
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
class WebSecurityConfig(
        private val providerSettings: ProviderSettings,
        private val redisTemplate: RedisTemplate<String, String?>) {

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity,
                                   accountUserDetailsService: AccountUserDetailsService): SecurityFilterChain {
        http.csrf().disable()
                .formLogin()
                .loginProcessingUrl("/login")
                .loginPage(LOG_IN_URL_PAGE)
                .permitAll()

        http.logout()
                .addLogoutHandler(ClearSessionStateLogoutHandler(SessionManagementFactory(providerSettings), redisTemplate))
                .invalidateHttpSession(true)

        http.requestMatchers().antMatchers(*WHITE_LIST)
                .and()
                .authorizeRequests()
                .mvcMatchers("/api/accounts")
                .permitAll()
                .and()

                .authorizeRequests()
                .mvcMatchers("/api/sign-up/mail/{mail}/welcome")
                .hasAnyAuthority(Scope.WELCOME.content)
                .and()

                .authorizeRequests()
                .mvcMatchers("/api/mail/{mail}/verify-challenge")
                .hasAnyAuthority(Scope.MAIL_VERIFY.content)
                .and()

                .authorizeRequests()
                .mvcMatchers("/api/mail/{mail}/rest-password-challenge")
                .permitAll()
                .and()

                .authorizeRequests()
                .mvcMatchers("/api/reset-password/{ticket}")
                .permitAll()
                .and()

                .authorizeRequests()
                .mvcMatchers("/api/**")
                .hasAnyAuthority(adminRole)
                .and()

        http.userDetailsService(accountUserDetailsService)
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter())

        http.csrf().disable().headers().frameOptions().sameOrigin()
        return http.build()
    }

    fun jwtAuthenticationConverter(): JwtAuthenticationConverter? {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter { jwt: Jwt ->
            val scope = jwt.getClaim<List<String>>("scope")
                    .filter { scope -> scope != Scope.OPEN_ID.content }
                    .filter { scope -> scope != Scope.EMAIL.content }
                    .filter { scope -> scope != Scope.PROFILE.content }
                    .map { role: String -> SimpleGrantedAuthority(role) }

            val authoritiesClaims = jwt.getClaim<List<String>>("authorities")
                    .map { role: String -> SimpleGrantedAuthority(role) }

            println( authoritiesClaims + scope)
            authoritiesClaims + scope
        }
        jwtAuthenticationConverter.setPrincipalClaimName("user_name")
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