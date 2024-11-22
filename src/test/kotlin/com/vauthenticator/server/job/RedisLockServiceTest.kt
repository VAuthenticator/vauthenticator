package com.vauthenticator.server.job

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class RedisLockServiceTest {

    @MockK
    lateinit var redisTemplate: RedisTemplate<String, String>

    @Test
    fun `when lock and unlock`() {
        val uut = RedisLockService(redisTemplate)

        every { redisTemplate.opsForValue() } returns mockk<ValueOperations<String, String>> {
            every { setIfAbsent("lockKey", "locked", 100, TimeUnit.MILLISECONDS) } returns true
            every { getAndDelete("lockKey") } returns "lockKey"
        }

        uut.lock(100)
        uut.unlock()
    }
}