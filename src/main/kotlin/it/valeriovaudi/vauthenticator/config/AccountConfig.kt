package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.repository.AccountRepositoryWithPasswordPolicy
import it.valeriovaudi.vauthenticator.account.repository.CachedAccountRepository
import it.valeriovaudi.vauthenticator.account.repository.DynamoDbAccountRepository
import it.valeriovaudi.vauthenticator.cache.CacheOperation
import it.valeriovaudi.vauthenticator.cache.RedisCacheOperation
import it.valeriovaudi.vauthenticator.password.PasswordPolicy
import it.valeriovaudi.vauthenticator.role.DynamoDbRoleRepository
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
        passwordPolicy: PasswordPolicy,
        dynamoDbClient: DynamoDbClient,
        accountCacheOperation: CacheOperation<String, Account>,
        @Value("\${vauthenticator.dynamo-db.account.table-name}") accountTableName: String,
        @Value("\${vauthenticator.dynamo-db.account.role.table-name}") accountRoleTableName: String
    ) =
        CachedAccountRepository(
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
        @Value("\${vauthenticator.dynamo-db.account.cache.ttl}") accountCacheTtl: Duration,
        @Value("\${vauthenticator.dynamo-db.account.cache.name}") accountCacheRegionName: String,
    ) = RedisCacheOperation<String, Account>(
        cacheName = accountCacheRegionName,
        ttl = accountCacheTtl,
        redisTemplate = redisTemplate as RedisTemplate<String, Account>
    )
}