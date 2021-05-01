package it.valeriovaudi.vauthenticator.oauth2.authorizationservice

import it.valeriovaudi.vauthenticator.extentions.toSha256
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.lang.Nullable
import org.springframework.security.oauth2.core.AbstractOAuth2Token
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.OAuth2RefreshToken
import org.springframework.security.oauth2.core.OAuth2TokenType
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.util.Assert
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class RedisOAuth2AuthorizationService(private val redisTemplate: RedisTemplate<Any, Any>) :
    OAuth2AuthorizationService {

    val logger: Logger = LoggerFactory.getLogger(RedisOAuth2AuthorizationService::class.java)

    override fun save(authorization: OAuth2Authorization) {
        Assert.notNull(authorization, "authorization cannot be null")

        redisTemplate.opsForHash<String, OAuth2Authorization>()
            .put(authorization.id, authorization.id.toSha256(), authorization)

        val findByTokenFor = findByTokenFor(authorization)
        if (findByTokenFor != null) {
            redisTemplate.opsForHash<String, String>()
                .put(findByTokenFor, findByTokenFor.toSha256(), authorization.id)
        }
        logger.info("save")
    }

    override fun remove(authorization: OAuth2Authorization) {
        Assert.notNull(authorization, "authorization cannot be null")
        logger.info("remove")
        redisTemplate.opsForHash<String, OAuth2Authorization>()
            .delete(authorization.id, authorization.id.toSha256(), authorization)
    }

    @Nullable
    override fun findById(id: String): OAuth2Authorization? {
        Assert.hasText(id, "id cannot be empty")
        logger.info("findById: $id")
        return redisTemplate.opsForHash<String, OAuth2Authorization>()
            .get(id, id.toSha256())
    }

    @Nullable
    override fun findByToken(token: String, @Nullable tokenType: OAuth2TokenType?): OAuth2Authorization? {
        Assert.hasText(token, "token cannot be empty")
        logger.info("findByToken")

        val id = redisTemplate.opsForHash<String, String>().get(token, token.toSha256())!!
        return redisTemplate.opsForHash<String, OAuth2Authorization>().get(id, id.toSha256())
    }

    fun findByTokenFor(
        authorization: OAuth2Authorization,
    ): String? {
        val state = authorization.getAttribute(OAuth2ParameterNames.STATE) as String?
        return if (state != null) {
            state
        } else {
            listOf(
                OAuth2AuthorizationCode::class.java,
                OAuth2AccessToken::class.java,
                OAuth2RefreshToken::class.java
            ).map { authorization.getToken(it)?.token?.tokenValue }
                .first()
        }
    }
}