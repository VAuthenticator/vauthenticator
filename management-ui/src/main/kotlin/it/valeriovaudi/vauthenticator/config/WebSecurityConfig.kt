package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.security.clientsecuritystarter.filter.BearerTokenInterceptor
import it.valeriovaudi.vauthenticator.security.clientsecuritystarter.filter.OAuth2TokenResolver
import it.valeriovaudi.vauthenticator.security.clientsecuritystarter.user.VAuthenticatorOAuth2User
import it.valeriovaudi.vauthenticator.security.clientsecuritystarter.user.VAuthenticatorOidcUserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.CustomUserTypesOAuth2UserService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.client.RestTemplate

const val adminRole = "VAUTHENTICATOR_ADMIN"

@Configuration(proxyBeanMethods = false)
class WebSecurityConfig {

    @Value("\${vauthenticator.client.registrationId}")
    private lateinit var registrationId: String

    @Bean
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable()

        http.logout()
                .deleteCookies("opbs")
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/secure/admin/index")

        http.authorizeRequests()
                .mvcMatchers("/secure/**")
                .hasAuthority(adminRole)
                .anyRequest().authenticated()


        http.authorizeRequests().mvcMatchers("/actuator/**", "/oidc_logout.html").permitAll()
                .and()
                .authorizeRequests().anyRequest().hasAnyRole(adminRole)
                .and().oauth2Login().defaultSuccessUrl("/index")
                .userInfoEndpoint()
                .oidcUserService(vAuthenticatorOidcUserService())


        return http.build()

    }

    fun vAuthenticatorOidcUserService(): VAuthenticatorOidcUserService =
            VAuthenticatorOidcUserService(OidcUserService(),
                    CustomUserTypesOAuth2UserService(mapOf(registrationId to VAuthenticatorOAuth2User::class.java)))


    @Bean
    fun budgetRestTemplate(oAuth2TokenResolver: OAuth2TokenResolver): RestTemplate {
        return RestTemplateBuilder()
                .additionalInterceptors(BearerTokenInterceptor(oAuth2TokenResolver))
                .build()
    }
}