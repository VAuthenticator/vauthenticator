package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.AccountRepository
import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Import(OAuth2AuthorizationServerConfiguration::class)
class WebSecurityConfig {

    private val LOG_IN_URL_PAGE = "/login"
    private val WHITE_LIST = arrayOf(
            "/logout",
            "/oidc/logout",
            "/login",
            "/user-info",
            "/webjars/**",
            "/api/**",
            "/secure/**"
    )

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity,
                                   accountUserDetailsService : AccountUserDetailsService): SecurityFilterChain {
        http.csrf().disable()
                .formLogin()
                .loginProcessingUrl("/login")
                .loginPage(LOG_IN_URL_PAGE)
                .permitAll()

        http.logout().logoutSuccessUrl("/secure/admin/index")

        http.requestMatchers().antMatchers(*WHITE_LIST)
                .and()
                .authorizeRequests()
                .mvcMatchers("/api/**", "/secure/**")
                .hasAuthority("VAUTHENTICATOR_ADMIN")

        http.userDetailsService(accountUserDetailsService)
        http.oauth2ResourceServer().jwt()
        return http.build()

    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(12)
    }

    @Bean
    fun accountUserDetailsService(userRepository: AccountRepository) =
            AccountUserDetailsService(userRepository)
}