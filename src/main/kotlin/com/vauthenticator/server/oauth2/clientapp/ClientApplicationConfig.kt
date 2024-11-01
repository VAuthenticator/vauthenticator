package com.vauthenticator.server.oauth2.clientapp

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.account.adapter.jdbc.JdbcAccountRepository
import com.vauthenticator.server.account.domain.AccountRepository
import com.vauthenticator.server.cache.CacheOperation
import com.vauthenticator.server.cache.RedisCacheOperation
import com.vauthenticator.server.oauth2.clientapp.adapter.cache.CachedClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.adapter.cache.ClientApplicationCacheContentConverter
import com.vauthenticator.server.oauth2.clientapp.adapter.dynamodb.DynamoDbClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.adapter.jdbc.JdbcClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.domain.ReadClientApplication
import com.vauthenticator.server.oauth2.clientapp.domain.StoreClientApplication
import com.vauthenticator.server.password.domain.VAuthenticatorPasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Duration

@Configuration(proxyBeanMethods = false)
class ClientApplicationConfig {


    @Bean("clientApplicationRepository")
    @Profile("aws")
    fun jdbcClientApplicationRepository(jdbcTemplate: JdbcTemplate, objectMapper: ObjectMapper) : ClientApplicationRepository =
        JdbcClientApplicationRepository(jdbcTemplate, objectMapper)

    @Bean("clientApplicationRepository")
    @ConditionalOnProperty(
        name = ["vauthenticator.dynamo-db.client-application.cache.enabled"],
        havingValue = "false",
        matchIfMissing = true
    )
    @Profile("!aws")
    fun dynamoDbClientApplicationRepository(
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.client-application.table-name}") clientAppTableName: String
    ) = DynamoDbClientApplicationRepository(dynamoDbClient, clientAppTableName)

    @Bean("clientApplicationRepository")
    @ConditionalOnProperty(
        name = ["vauthenticator.dynamo-db.client-application.cache.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    @Profile("!aws")
    fun cachedClientApplicationRepository(
        dynamoDbClient: DynamoDbClient,
        clientApplicationCacheOperation: CacheOperation<String, String>,
        objectMapper: ObjectMapper,
        @Value("\${vauthenticator.dynamo-db.client-application.cache.enabled:false}") withCash: Boolean,
        @Value("\${vauthenticator.dynamo-db.client-application.table-name}") clientAppTableName: String
    ) = CachedClientApplicationRepository(
        ClientApplicationCacheContentConverter(objectMapper),
        clientApplicationCacheOperation,
        DynamoDbClientApplicationRepository(dynamoDbClient, clientAppTableName)
    )

    @Bean
    @ConditionalOnProperty(
        name = ["vauthenticator.dynamo-db.client-application.cache.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    @Profile("!aws")
    fun clientApplicationCacheOperation(
        redisTemplate: RedisTemplate<*, *>,
        @Value("\${vauthenticator.dynamo-db.client-application.cache.ttl}") ttl: Duration,
        @Value("\${vauthenticator.dynamo-db.client-application.cache.name}") cacheRegionName: String,
    ) = RedisCacheOperation<String, String>(
        cacheName = cacheRegionName,
        ttl = ttl,
        redisTemplate = redisTemplate as RedisTemplate<String, String>
    )

    @Bean
    fun readClientApplication(clientApplicationRepository: ClientApplicationRepository) =
        ReadClientApplication(clientApplicationRepository)

    @Bean
    fun storeClientApplication(
        clientApplicationRepository: ClientApplicationRepository,
        passwordEncoder: VAuthenticatorPasswordEncoder
    ) =
        StoreClientApplication(clientApplicationRepository, passwordEncoder)

}