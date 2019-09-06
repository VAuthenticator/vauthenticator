package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.openidconnect.nonce.AddNonceInAuthorizeResponseInterceptor
import it.valeriovaudi.vauthenticator.openidconnect.nonce.InMemoryNonceStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    @Autowired
    lateinit var nonceStore: InMemoryNonceStore;

    @Bean
    fun addNonceInAuthorizeResponseInterceptor() =
            AddNonceInAuthorizeResponseInterceptor(nonceStore)

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(addNonceInAuthorizeResponseInterceptor())
                .addPathPatterns("/oauth/authorize")
    }
}