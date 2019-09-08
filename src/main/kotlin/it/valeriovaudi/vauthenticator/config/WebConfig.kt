package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.openid.connect.nonce.AddNonceInAuthorizeResponseInterceptor
import it.valeriovaudi.vauthenticator.openid.connect.nonce.NonceStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    @Autowired
    lateinit var nonceStore: NonceStore;

    @Bean
    fun addNonceInAuthorizeResponseInterceptor() =
            AddNonceInAuthorizeResponseInterceptor(nonceStore)

}