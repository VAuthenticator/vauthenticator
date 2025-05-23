package com.vauthenticator.server.oauth2.clientapp.adapter.redis

import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOrigin
import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOriginRepository
import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOrigins
import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import org.springframework.data.redis.core.RedisTemplate

class RedisAllowedOriginRepository(val redisTemplate: RedisTemplate<String, String>) :
    AllowedOriginRepository {

    override fun getAllAvailableAllowedOrigins(): Set<AllowedOrigin> {
TODO()
//        return redisTemplate.flatMap { it.value.content.toList() }.toSet()
    }

    override fun setAllowedOriginsFor(clientAppId: ClientAppId, allowedOrigins: AllowedOrigins) {
//        redisTemplate.put(clientAppId, allowedOrigins)
        TODO()
    }

    override fun deleteAllowedOriginsFor(clientAppId: ClientAppId) {
//        redisTemplate.remove(clientAppId)
        TODO()
    }

}