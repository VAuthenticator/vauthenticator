package it.valeriovaudi.vauthenticator.oauth2.codeservice

import it.valeriovaudi.vauthenticator.extentions.toSha256
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices

class RedisAuthorizationCodeServices(private val redisTemplate: RedisTemplate<String, OAuth2Authentication>) : RandomValueAuthorizationCodeServices() {

    override fun store(code: String, authentication: OAuth2Authentication) {
        this.redisTemplate.opsForHash<Any, Any>().put(code, code.toSha256(), authentication)
    }

    override fun remove(code: String): OAuth2Authentication {
        val oAuth2Authentication = this.redisTemplate.opsForHash<Any, Any>().get(code, code.toSha256()) as OAuth2Authentication
        this.redisTemplate.opsForHash<Any, Any>().delete(code, code.toSha256())

        return oAuth2Authentication
    }


}