package it.valeriovaudi.vauthenticator.codeservice

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

val logger = LoggerFactory.getLogger(RedisAuthorizationCodeServices::class.java.name)

class RedisAuthorizationCodeServices(private val redisTemplate: RedisTemplate<String, OAuth2Authentication>) : RandomValueAuthorizationCodeServices() {

    override fun store(code: String, authentication: OAuth2Authentication) {
        this.redisTemplate.opsForHash<Any, Any>().put(code, toSha(code), authentication)
    }

    override fun remove(code: String): OAuth2Authentication {
        val oAuth2Authentication = this.redisTemplate.opsForHash<Any, Any>().get(code, toSha(code)) as OAuth2Authentication
        this.redisTemplate.opsForHash<Any, Any>().delete(code, toSha(code))
        return oAuth2Authentication
    }

    private fun toSha(code: String): String {
        var messageDigest: MessageDigest? = null
        try {
            messageDigest = MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            logger.error(e.message, e)
        }

        return String(messageDigest!!.digest(code.toByteArray()))
    }
}