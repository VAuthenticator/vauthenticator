package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

const val adminRole = "VAUTHENTICATOR_ADMIN"

private const val LOG_IN_URL_PAGE = "/login"
private val WHITE_LIST = arrayOf(
        "/logout",
        "/oidc/logout",
        "/login",
        "/webjars/**",
        "/api/**",
        "/secure/**"
)

@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity,
                                   accountUserDetailsService: AccountUserDetailsService): SecurityFilterChain {
        http.csrf().disable()
                .formLogin()
                .loginProcessingUrl("/login")
                .loginPage(LOG_IN_URL_PAGE)
                .permitAll()

        http.logout()
                .deleteCookies("opbs")
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/secure/admin/index")

        http.requestMatchers().antMatchers(*WHITE_LIST)
                .and()
                .authorizeRequests()
                .mvcMatchers("/api/accounts/**", "/secure/**")
                .hasAuthority(adminRole)
                .and()
                .authorizeRequests()
                .mvcMatchers("/api/signup")
                .authenticated()


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