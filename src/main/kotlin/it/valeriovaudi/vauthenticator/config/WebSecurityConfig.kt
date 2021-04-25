package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.AccountRepository
import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration.applyDefaultSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
class WebSecurityConfig {

    private val LOG_IN_URL_PAGE = "/login"
    private val WHITE_LIST = arrayOf(
        "/logout",
        "/oidc/logout",
        "/login",
        "/user-info",
        "/webjars/**"
    )

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable()
            .formLogin()
            .loginProcessingUrl("/login")
            .loginPage(LOG_IN_URL_PAGE)
            .permitAll()
            .and()
            .logout().logoutSuccessUrl("/secure/admin/index")
            .and()
            .requestMatchers().antMatchers(*WHITE_LIST)
            .and()
            .requestMatchers().antMatchers("/api/**", "/secure/**")
            .and()
            .authorizeRequests()
            .mvcMatchers("/api/**", "/secure/**")
            .hasAuthority("VAUTHENTICATOR_ADMIN")
            .and()
            .authorizeRequests().anyRequest().permitAll()
            .and().oauth2ResourceServer().jwt()
        return http.build()

    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


    @Bean
    fun accountUserDetailsService(
        passwordEncoder: PasswordEncoder,
        userRepository: AccountRepository
    ) = AccountUserDetailsService(userRepository)
}