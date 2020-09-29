package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.openid.connect.nonce.AddNonceInAuthorizeResponseInterceptor
import it.valeriovaudi.vauthenticator.openid.connect.nonce.NonceStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebConfig {

    @Bean
    fun addNonceInAuthorizeResponseInterceptor(nonceStore: NonceStore) =
            AddNonceInAuthorizeResponseInterceptor(nonceStore)

}