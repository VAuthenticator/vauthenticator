package it.valeriovaudi.vauthenticator.cache

import it.valeriovaudi.vauthenticator.extentions.toSha256
import it.valeriovaudi.vauthenticator.support.RedisUtils.aRedisTemplate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration
import java.util.*

class RedisCacheOperationTest {
    lateinit var underTest: CacheOperation<String, String>
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private val a_cache = "a_cache"

    @BeforeEach
    fun setUp() {
        redisTemplate = aRedisTemplate()
        underTest = RedisCacheOperation(a_cache, redisTemplate)
    }


    @Test
    fun `when we are able to put an object in cache`() {
        val key = "a_key"
        val expected = "a_value"
        val ttl = Duration.ofSeconds(10)
        underTest.put(key, expected, ttl)

        val actual = redisTemplate.opsForHash<String, String>().get("a_cache_$key", "a_cache_$key".toSha256())
        val actualExpire = redisTemplate.opsForHash<String, String>().operations.getExpire("a_cache_$key")

        Assertions.assertEquals(expected, actual)
        Assertions.assertEquals(ttl.seconds, actualExpire)
    }

    @Test
    fun `when we are able to get an object in cache`() {
        val key = "a_key"
        val value = "a_value"

        redisTemplate.opsForHash<String, String>().put("a_cache_$key", "a_cache_$key".toSha256(), value)
        val actual = underTest.get(key)

        Assertions.assertEquals(Optional.of(value), actual)
    }

    @Test
    fun `when we are able to evict an object in cache`() {
        val key = "a_key"
        val value = "a_value"

        redisTemplate.opsForHash<String, String>().put("a_cache_$key", "a_cache_$key".toSha256(), value)
        underTest.evict(key)
        val actual = Optional.ofNullable(redisTemplate.opsForHash<String, String>().get("a_cache_$key", "a_cache_$key".toSha256()))

        Assertions.assertEquals(Optional.empty<String>(), actual)
    }
}