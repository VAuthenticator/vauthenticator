package it.valeriovaudi.vauthenticator.openid.connect.nonce

import it.valeriovaudi.vauthenticator.extentions.toSha256
import org.springframework.data.redis.core.RedisTemplate

class RedisNonceStore(private val redisTemplate: RedisTemplate<String, String>) : NonceStore {

    override fun load(key: String): String =
            this.redisTemplate.opsForHash<String, String>().get(key, key.toSha256()).orEmpty()


    override fun store(key: String, nonce: String) {
        this.redisTemplate.opsForHash<String, String>().put(key, key.toSha256(), nonce)
    }

}