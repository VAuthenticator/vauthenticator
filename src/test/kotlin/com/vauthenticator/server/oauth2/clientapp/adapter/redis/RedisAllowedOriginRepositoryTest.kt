package com.vauthenticator.server.oauth2.clientapp.adapter.redis

import com.vauthenticator.server.oauth2.clientapp.adapter.AbstractAllowedOriginRepositoryTest
import com.vauthenticator.server.oauth2.clientapp.domain.AllowedOriginRepository
import com.vauthenticator.server.support.RedisUtils

class RedisAllowedOriginRepositoryTest : AbstractAllowedOriginRepositoryTest() {
    val redisTemplate = RedisUtils.aRedisTemplate()

    override fun resetDatabase() {
//        redisTemplate.opsForList().
    }

    override fun initUnitUnderTest(): AllowedOriginRepository {
        return RedisAllowedOriginRepository(redisTemplate)
    }


}