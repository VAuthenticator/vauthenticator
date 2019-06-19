package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.integration.LogInRequestGateway
import it.valeriovaudi.vauthenticator.security.AccountUserDetailsService
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    companion object {

        private val LOG_IN_URL_PAGE = "/singin"
        private val WHITE_LIST = arrayOf("/singin", "/user-info", "/sign-key/public", "/oauth/authorize", "/oauth/confirm_access", "/webjars/**")
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .formLogin().loginPage(LOG_IN_URL_PAGE)
                .loginProcessingUrl(LOG_IN_URL_PAGE)
                .permitAll()
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
    fun accountUserDetailsService(logInRequestGateway: LogInRequestGateway) =
            AccountUserDetailsService(logInRequestGateway)


}