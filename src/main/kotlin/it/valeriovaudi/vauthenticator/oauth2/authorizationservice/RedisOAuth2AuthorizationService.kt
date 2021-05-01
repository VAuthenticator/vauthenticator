package it.valeriovaudi.vauthenticator.oauth2.authorizationservice

import it.valeriovaudi.vauthenticator.extentions.toSha256
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.lang.Nullable
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
import kotlin.math.log

class RedisOAuth2AuthorizationService(private val redisTemplate: RedisTemplate<Any, Any>) :
    OAuth2AuthorizationService {

    val logger = LoggerFactory.getLogger(RedisOAuth2AuthorizationService::class.java)
    private val authorizations: MutableMap<String, OAuth2Authorization?> = ConcurrentHashMap()

    override fun save(authorization: OAuth2Authorization) {
        Assert.notNull(authorization, "authorization cannot be null")

        redisTemplate.opsForHash<String, OAuth2Authorization>()
            .put(authorization.id, authorization.id.toSha256(), authorization)
        logger.info("save")

        Optional.ofNullable(
            authorization.attributes["org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest"] as OAuth2AuthorizationRequest
        ).ifPresent{
            logger.info("additionalParameters: ${it.additionalParameters}")
            logger.info(it.state)
            logger.info("attributes: ${it.attributes}")
            logger.info("attributes: ${it.responseType.value}")
        }

//        org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
        logger.info("attributes: ${authorization.attributes}")
        logger.info("accessToken ${authorization.accessToken}")
        logger.info("refreshToken ${authorization.refreshToken}")
        logger.info("registeredClientId ${authorization.registeredClientId}")
        logger.info("authorizationGrantType ${authorization.authorizationGrantType.value}")
        logger.info("principalName ${authorization.principalName}")
        logger.info("id ${authorization.id}")
        authorizations[authorization.id] = authorization
    }

    override fun remove(authorization: OAuth2Authorization) {
        Assert.notNull(authorization, "authorization cannot be null")
        println("save")
        println("attributes: ${authorization.attributes}")
        println("accessToken ${authorization.accessToken}")
        println("refreshToken ${authorization.refreshToken}")
        println("registeredClientId ${authorization.registeredClientId}")
        println("authorizationGrantType ${authorization.authorizationGrantType.value}")
        println("principalName ${authorization.principalName}")
        println("id ${authorization.id}")
        redisTemplate.opsForHash<String, OAuth2Authorization>()
            .delete(authorization.id, authorization.id.toSha256(), authorization)
        authorizations.remove(authorization.id, authorization)

    }

    @Nullable
    override fun findById(id: String): OAuth2Authorization? {
        Assert.hasText(id, "id cannot be empty")
        logger.info("findById: $id")
         redisTemplate.opsForHash<String, OAuth2Authorization>()
            .get(id, id.toSha256())
        return authorizations[id]

    }

    @Nullable
    override fun findByToken(token: String, @Nullable tokenType: OAuth2TokenType?): OAuth2Authorization? {
        Assert.hasText(token, "token cannot be empty")
        logger.info("findByToken: $token")
        logger.info("findByToken: ${tokenType?.value}")

        return authorizations.values.stream()
            .filter { authorization: OAuth2Authorization? ->
                hasToken(
                    authorization,
                    token,
                    tokenType
                )
            }
            .findFirst()
            .orElse(null)
    }

    private fun hasToken(
        authorization: OAuth2Authorization?,
        token: String,
        @Nullable tokenType: OAuth2TokenType?
    ): Boolean {
        if (tokenType == null) {
            return matchesState(authorization, token) ||
                    matchesAuthorizationCode(authorization, token) ||
                    matchesAccessToken(authorization, token) ||
                    matchesRefreshToken(authorization, token)
        } else if (OAuth2ParameterNames.STATE == tokenType.value) {
            return matchesState(authorization, token)
        } else if (OAuth2ParameterNames.CODE == tokenType.value) {
            return matchesAuthorizationCode(authorization, token)
        } else if (OAuth2TokenType.ACCESS_TOKEN == tokenType) {
            return matchesAccessToken(authorization, token)
        } else if (OAuth2TokenType.REFRESH_TOKEN == tokenType) {
            return matchesRefreshToken(authorization, token)
        }
        return false
    }

    private fun matchesState(authorization: OAuth2Authorization?, token: String): Boolean {
        return token == authorization!!.getAttribute(OAuth2ParameterNames.STATE)
    }

    private fun matchesAuthorizationCode(authorization: OAuth2Authorization?, token: String): Boolean {
        val authorizationCode = authorization!!.getToken(
            OAuth2AuthorizationCode::class.java
        )
        return authorizationCode != null && authorizationCode.token.tokenValue == token
    }

    private fun matchesAccessToken(authorization: OAuth2Authorization?, token: String): Boolean {
        val accessToken = authorization!!.getToken(
            OAuth2AccessToken::class.java
        )
        return accessToken != null && accessToken.token.tokenValue == token
    }

    private fun matchesRefreshToken(authorization: OAuth2Authorization?, token: String): Boolean {
        val refreshToken = authorization!!.getToken(
            OAuth2RefreshToken::class.java
        )
        return refreshToken != null && refreshToken.token.tokenValue == token
    }
}