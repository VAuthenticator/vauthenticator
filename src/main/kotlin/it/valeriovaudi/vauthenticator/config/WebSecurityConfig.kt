package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.mfa.MfaAuthentication
import it.valeriovaudi.vauthenticator.mfa.MfaAuthenticationHandler
import it.valeriovaudi.vauthenticator.mfa.MfaTrustResolver
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope
import it.valeriovaudi.vauthenticator.openid.connect.logout.ClearSessionStateLogoutHandler
import it.valeriovaudi.vauthenticator.openid.connect.sessionmanagement.SessionManagementFactory
import it.valeriovaudi.vauthenticator.password.BcryptVAuthenticatorPasswordEncoder
import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpMethod
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.ObjectPostProcessor
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import java.util.function.Supplier


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
        http: HttpSecurity,
        clientApplicationRepository: ClientApplicationRepository,
        mfaAuthorizationManager: AuthorizationManager<RequestAuthorizationContext>,
        accountUserDetailsService: AccountUserDetailsService
    ): SecurityFilterChain {
        http.csrf().disable().headers().frameOptions().disable()

        http.formLogin()
            .successHandler(MfaAuthenticationHandler(clientApplicationRepository, "/mfa-challenge"))
            .loginProcessingUrl(LOG_IN_URL_PAGE)
            .loginPage(LOG_IN_URL_PAGE)
            .permitAll()

        http.exceptionHandling {
            it.withObjectPostProcessor(object : ObjectPostProcessor<ExceptionTranslationFilter> {
                override fun <O : ExceptionTranslationFilter?> postProcess(filter: O): O {
                    filter!!.setAuthenticationTrustResolver(MfaTrustResolver())
                    return filter
                }
            })
        }
            .securityContext { it.requireExplicitSave(false) };

        http.logout()
            .addLogoutHandler(ClearSessionStateLogoutHandler(SessionManagementFactory(providerSettings), redisTemplate))
            .invalidateHttpSession(true)

        http.userDetailsService(accountUserDetailsService)
        http.oauth2ResourceServer().jwt()
        http.securityMatcher(*WHITE_LIST, "/api/**", "/mfa-challenge/**")
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/mfa-challenge/send").permitAll()
                    .requestMatchers("/mfa-challenge").access(mfaAuthorizationManager)
                    .requestMatchers(*WHITE_LIST).permitAll()
                    .requestMatchers("/api/accounts").permitAll()
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
    fun mfaAuthorizationManager(): AuthorizationManager<RequestAuthorizationContext> {
        return AuthorizationManager { authentication: Supplier<Authentication>, _: RequestAuthorizationContext ->
            AuthorizationDecision(
                authentication.get() is MfaAuthentication
            )
        }
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

    @Bean
    fun successHandler(): AuthenticationSuccessHandler {
        return SavedRequestAwareAuthenticationSuccessHandler()
    }

    @Bean
    fun failureHandler(): AuthenticationFailureHandler {
        return SimpleUrlAuthenticationFailureHandler("/login?error")
    }
}