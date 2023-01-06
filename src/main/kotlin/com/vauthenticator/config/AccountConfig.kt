package com.vauthenticator.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.account.AccountCacheContentConverter
import com.vauthenticator.account.repository.AccountRepositoryWithPasswordPolicy
import com.vauthenticator.account.repository.CachedAccountRepository
import com.vauthenticator.account.repository.DynamoDbAccountRepository
import com.vauthenticator.cache.CacheOperation
import com.vauthenticator.cache.RedisCacheOperation
import com.vauthenticator.password.PasswordPolicy
import com.vauthenticator.role.DynamoDbRoleRepository
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
        passwordPolicy: PasswordPolicy,
        dynamoDbClient: DynamoDbClient,
        accountCacheOperation: CacheOperation<String, String>,
        @Value("\${vauthenticator.dynamo-db.account.table-name}") accountTableName: String,
        @Value("\${vauthenticator.dynamo-db.account.role.table-name}") accountRoleTableName: String
    ) =
        CachedAccountRepository(
            AccountCacheContentConverter(mapper),
            accountCacheOperation,
            AccountRepositoryWithPasswordPolicy(
                DynamoDbAccountRepository(dynamoDbClient, accountTableName, accountRoleTableName),
                passwordPolicy
            )
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