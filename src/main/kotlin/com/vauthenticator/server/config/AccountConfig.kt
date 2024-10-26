package com.vauthenticator.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.account.adapter.CachedAccountRepository
import com.vauthenticator.server.account.adapter.dynamodb.DynamoDbAccountRepository
import com.vauthenticator.server.account.adapter.jdbc.JdbcAccountRepository
import com.vauthenticator.server.account.domain.AccountCacheContentConverter
import com.vauthenticator.server.account.domain.AccountRepository
import com.vauthenticator.server.account.domain.AccountUpdateAdminAction
import com.vauthenticator.server.account.domain.SaveAccount
import com.vauthenticator.server.cache.CacheOperation
import com.vauthenticator.server.cache.RedisCacheOperation
import com.vauthenticator.server.role.domain.RoleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Duration

@Configuration(proxyBeanMethods = false)
class AccountConfig {

    @Bean
    fun changeAccountEnabling(accountRepository: AccountRepository): AccountUpdateAdminAction =
        AccountUpdateAdminAction(accountRepository)

    @Bean
    fun saveAccount(accountRepository: AccountRepository): SaveAccount =
        SaveAccount(accountRepository)


    @Bean("accountRepository")
    @Profile("experimental_database_persistence")
    fun jdbcAccountRepository(
        jdbcTemplate: JdbcTemplate
    ) = JdbcAccountRepository(jdbcTemplate)


    @Bean("accountRepository")
    @ConditionalOnProperty(
        name = ["vauthenticator.dynamo-db.account.cache.enabled"],
        havingValue = "false",
        matchIfMissing = true
    )
    @Profile("!experimental_database_persistence")
    fun dynamoDbAccountRepository(
        mapper: ObjectMapper,
        dynamoDbClient: DynamoDbClient,
        roleRepository: RoleRepository,
        @Value("\${vauthenticator.dynamo-db.account.table-name}") accountTableName: String,
        @Value("\${vauthenticator.dynamo-db.account.role.table-name}") accountRoleTableName: String
    ) =
        DynamoDbAccountRepository(dynamoDbClient, accountTableName, roleRepository)


    @Bean("accountRepository")
    @ConditionalOnProperty(
        name = ["vauthenticator.dynamo-db.account.cache.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    @Profile("!experimental_database_persistence")
    fun cachedDynamoDbAccountRepository(
        mapper: ObjectMapper,
        dynamoDbClient: DynamoDbClient,
        accountCacheOperation: CacheOperation<String, String>,
        roleRepository: RoleRepository,
        @Value("\${vauthenticator.dynamo-db.account.table-name}") accountTableName: String,
        @Value("\${vauthenticator.dynamo-db.account.role.table-name}") accountRoleTableName: String
    ) =
        CachedAccountRepository(
            AccountCacheContentConverter(mapper),
            accountCacheOperation,
            DynamoDbAccountRepository(dynamoDbClient, accountTableName, roleRepository),
        )

    @Bean
    @ConditionalOnProperty(
        name = ["vauthenticator.dynamo-db.account.cache.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    @Profile("!experimental_database_persistence")
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