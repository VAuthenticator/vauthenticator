package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope
import it.valeriovaudi.vauthenticator.openid.connect.logout.ClearSessionStateLogoutHandler
import it.valeriovaudi.vauthenticator.openid.connect.sessionmanagement.SessionManagementFactory
import it.valeriovaudi.vauthenticator.password.BcryptVAuthenticatorPasswordEncoder
import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.servlet.handler.HandlerMappingIntrospector


const val adminRole = "VAUTHENTICATOR_ADMIN"

private const val LOG_IN_URL_PAGE = "/login"
private val WHITE_LIST = arrayOf(
    "/logout",
    "/oidc/logout",
    "/login"
)

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
class WebSecurityConfig(
    private val providerSettings: AuthorizationServerSettings,
    private val redisTemplate: RedisTemplate<String, String?>
) {

    @Bean
    fun defaultSecurityFilterChain(
        introspector: HandlerMappingIntrospector,
        http: HttpSecurity,
        accountUserDetailsService: AccountUserDetailsService
    ): SecurityFilterChain {
        http.csrf().disable().headers().frameOptions().disable()

        http.formLogin()
            .loginProcessingUrl(LOG_IN_URL_PAGE)
            .loginPage(LOG_IN_URL_PAGE)
            .permitAll()

        http.logout()
            .addLogoutHandler(ClearSessionStateLogoutHandler(SessionManagementFactory(providerSettings), redisTemplate))
            .invalidateHttpSession(true)

        http.userDetailsService(accountUserDetailsService)
        http.oauth2ResourceServer().jwt()
        http.securityMatcher(*WHITE_LIST, "/api/**")
            .authorizeHttpRequests { authz ->
            authz
                .requestMatchers( *WHITE_LIST).permitAll()
                .requestMatchers( "/api/accounts").permitAll()
                .requestMatchers("/api/sign-up/mail/{mail}/welcome").hasAnyAuthority(Scope.WELCOME.content)
                .requestMatchers("/api/mail/{mail}/verify-challenge").hasAnyAuthority(Scope.MAIL_VERIFY.content)
                .requestMatchers("/api/mail/{mail}/rest-password-challenge").permitAll()
                .requestMatchers("/api/reset-password/{ticket}").permitAll()

                .requestMatchers(HttpMethod.GET, "/api/keys").hasAnyAuthority(Scope.KEY_READER.content)
                .requestMatchers(HttpMethod.POST, "/api/keys").hasAnyAuthority(Scope.KEY_EDITOR.content)
                .requestMatchers(HttpMethod.DELETE, "/api/keys").hasAnyAuthority(Scope.KEY_EDITOR.content)
                .requestMatchers("/api/**").hasAnyAuthority(adminRole)
        }

        return http.build()
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter { jwt: Jwt ->
            val scope = jwt.getClaim<List<String>>("scope")
                .filter { scope -> scope != Scope.OPEN_ID.content }
                .filter { scope -> scope != Scope.EMAIL.content }
                .filter { scope -> scope != Scope.PROFILE.content }
                .map { role: String -> SimpleGrantedAuthority(role) }

            val authoritiesClaims = jwt.getClaim<List<String>>("authorities")
                .map { role: String -> SimpleGrantedAuthority(role) }

            println(authoritiesClaims + scope)
            authoritiesClaims + scope
        }
        jwtAuthenticationConverter.setPrincipalClaimName("user_name")
        return jwtAuthenticationConverter
    }

    @Bean
    fun bcryptAccountPasswordEncoder(passwordEncoder: PasswordEncoder) =
        BcryptVAuthenticatorPasswordEncoder(passwordEncoder)

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(12)
    }

    @Bean
    fun accountUserDetailsService(userRepository: AccountRepository) =
        AccountUserDetailsService(userRepository)
}