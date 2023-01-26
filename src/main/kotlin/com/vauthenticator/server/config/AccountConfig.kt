package com.vauthenticator.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.account.AccountCacheContentConverter
import com.vauthenticator.server.account.repository.CachedAccountRepository
import com.vauthenticator.server.account.repository.DynamoDbAccountRepository
import com.vauthenticator.server.cache.CacheOperation
import com.vauthenticator.server.cache.RedisCacheOperation
import com.vauthenticator.server.role.DynamoDbRoleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Duration

@Configuration(proxyBeanMethods = false)
class AccountConfig {

    @Bean
    fun accountRepository(
        mapper: ObjectMapper,
        dynamoDbClient: DynamoDbClient,
        accountCacheOperation: CacheOperation<String, String>,
        @Value("\${vauthenticator.dynamo-db.account.table-name}") accountTableName: String,
        @Value("\${vauthenticator.dynamo-db.account.role.table-name}") accountRoleTableName: String
    ) =
        CachedAccountRepository(
            AccountCacheContentConverter(mapper),
            accountCacheOperation,
            DynamoDbAccountRepository(dynamoDbClient, accountTableName, accountRoleTableName),
        )

    @Bean
    fun roleRepository(
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.role.table-name}") roleTableName: String
    ) =
        DynamoDbRoleRepository(dynamoDbClient, roleTableName)


    @Bean
    fun accountCacheOperation(
        redisTemplate: RedisTemplate<*, *>,
        @Value("\${vauthenticator.dynamo-db.account.cache.ttl}") ttl: Duration,
        @Value("\${vauthenticator.dynamo-db.account.cache.name}") cacheRegionName: String,
    ) = RedisCacheOperation<String, String>(
        cacheName = cacheRegionName,
        ttl = ttl,
        redisTemplate = redisTemplate as RedisTemplate<String, String>
    )
}