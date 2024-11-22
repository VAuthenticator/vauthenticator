package com.vauthenticator.server.job

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit


interface LockService {

    fun lock(timeout: Long)

    fun unlock()

}

class RedisLockService(
    private val redisTemplate: RedisTemplate<String, String>
) : LockService {

    private val logger = LoggerFactory.getLogger(RedisLockService::class.java)

    override fun lock(timeout: Long) {
        if (!acquireLockWith(timeout)) {
            logger.info("lock already acquired")
            Thread.sleep(timeout)
        } else {
            logger.info("lock acquired")
        }
    }

    private fun acquireLockWith(timeout: Long): Boolean {
        logger.info("try to acquire Lock")
        return redisTemplate.opsForValue()
            .setIfAbsent("lockKey", "locked", timeout, TimeUnit.MILLISECONDS)!!
    }

    override fun unlock() {
        logger.info("lock released")
        redisTemplate.opsForValue().getAndDelete("lockKey")
    }

}