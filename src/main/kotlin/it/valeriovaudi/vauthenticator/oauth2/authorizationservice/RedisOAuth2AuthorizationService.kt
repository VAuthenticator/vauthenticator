package it.valeriovaudi.vauthenticator.oauth2.authorizationservice

import it.valeriovaudi.vauthenticator.extentions.toSha256
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.lang.Nullable
import org.springframework.security.oauth2.core.*
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
        logger.info("save")

        redisTemplate.opsForHash<String, OAuth2Authorization>()
            .put(authorization.id, authorization.id.toSha256(), authorization)

        val findByTokenFor = findByTokenFor(authorization)
        findByTokenFor.forEach {
            redisTemplate.opsForHash<String, String>()
                .put(it, it.toSha256(), authorization.id)
        }
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
    ): List<String> {
        val state = authorization.getAttribute(OAuth2ParameterNames.STATE) as String?
        return if (state != null) {
            listOf(state)
        } else {
            listOf(
                OAuth2AuthorizationCode::class.java,
                OAuth2AccessToken::class.java,
                OAuth2RefreshToken::class.java,
                OAuth2RefreshToken2::class.java
            ).mapNotNull { authorization.getToken(it) }
                .map { it.token.tokenValue }
        }
    }
}