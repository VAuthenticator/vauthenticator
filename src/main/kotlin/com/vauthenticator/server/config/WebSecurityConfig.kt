package com.vauthenticator.server.config

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.login.userdetails.AccountUserDetailsService
import com.vauthenticator.server.mfa.MfaAuthentication
import com.vauthenticator.server.mfa.MfaAuthenticationHandler
import com.vauthenticator.server.mfa.MfaTrustResolver
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.Scope
import com.vauthenticator.server.oidc.logout.ClearSessionStateLogoutHandler
import com.vauthenticator.server.oidc.sessionmanagement.SessionManagementFactory
import com.vauthenticator.server.password.BcryptVAuthenticatorPasswordEncoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
        http.csrf { it.disable() }
        http.headers { it.frameOptions { it.disable() } }

        http.formLogin {
            it.successHandler(MfaAuthenticationHandler(clientApplicationRepository, "/mfa-challenge/send"))
                .loginProcessingUrl(LOG_IN_URL_PAGE)
                .loginPage(LOG_IN_URL_PAGE)
                .permitAll()
        }

        http.exceptionHandling {
            it.withObjectPostProcessor(object : ObjectPostProcessor<ExceptionTranslationFilter> {
                override fun <O : ExceptionTranslationFilter?> postProcess(filter: O): O {
                    filter!!.setAuthenticationTrustResolver(MfaTrustResolver())
                    return filter
                }
            })
        }
            .securityContext { it.requireExplicitSave(false) };

        http.logout {
            it.addLogoutHandler(
                ClearSessionStateLogoutHandler(
                    SessionManagementFactory(providerSettings),
                    redisTemplate
                )
            )
                .invalidateHttpSession(true)

        }

        http.userDetailsService(accountUserDetailsService)
        http.oauth2ResourceServer { it.jwt {} }
        http.securityMatcher(*WHITE_LIST, "/api/**", "/mfa-challenge/**")
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/mfa-challenge/send").permitAll()
                    .requestMatchers("/mfa-challenge")
                    .access(mfaAuthorizationManager)

                    .requestMatchers(*WHITE_LIST).permitAll()
                    .requestMatchers("/api/accounts").permitAll()

                    .requestMatchers(HttpMethod.PUT, "/api/sign-up/mail/welcome")
                    .hasAnyAuthority(Scope.WELCOME.content)

                    .requestMatchers(HttpMethod.PUT, "/api/mail/verify-challenge")
                    .hasAnyAuthority(Scope.MAIL_VERIFY.content)

                    .requestMatchers(HttpMethod.PUT, "/api/password")
                    .hasAnyAuthority(Scope.CHANGE_PASSWORD.content)

                    .requestMatchers(HttpMethod.PUT, "/api/reset-password-challenge").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/api/reset-password/{ticket}").permitAll()

                    .requestMatchers(HttpMethod.GET, "/api/mail-template")
                    .hasAnyAuthority(Scope.MAIL_TEMPLATE_READER.content)

                    .requestMatchers(HttpMethod.PUT, "/api/mail-template")
                    .hasAnyAuthority(Scope.MAIL_TEMPLATE_WRITER.content)

                    .requestMatchers(HttpMethod.GET, "/api/keys")
                    .hasAnyAuthority(Scope.KEY_READER.content)

                    .requestMatchers(HttpMethod.POST, "/api/keys")
                    .hasAnyAuthority(Scope.KEY_EDITOR.content)

                    .requestMatchers(HttpMethod.DELETE, "/api/keys")
                    .hasAnyAuthority(Scope.KEY_EDITOR.content)

                    .requestMatchers("/api/**")
                    .hasAnyAuthority(adminRole)
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
        val logger: Logger = LoggerFactory.getLogger(JwtAuthenticationConverter::class.java)

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter { jwt: Jwt ->
            val scope = jwt.getClaim<List<String>>("scope")
                .filter { scope -> scope != Scope.OPEN_ID.content }
                .filter { scope -> scope != Scope.EMAIL.content }
                .filter { scope -> scope != Scope.PROFILE.content }
                .map { role: String -> SimpleGrantedAuthority(role) }

            val authoritiesClaims = jwt.getClaim<List<String>>("authorities")
                .map { role: String -> SimpleGrantedAuthority(role) }

            logger.debug("authorities: ${authoritiesClaims + scope}")
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
    fun mfaFailureHandler(): AuthenticationFailureHandler {
        return SimpleUrlAuthenticationFailureHandler("/mfa-challenge?error")
    }

    @Bean
    fun failureHandler(): AuthenticationFailureHandler {
        return SimpleUrlAuthenticationFailureHandler("/login?error")
    }
}