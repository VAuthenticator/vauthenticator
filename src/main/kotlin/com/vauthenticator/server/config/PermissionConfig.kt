package com.vauthenticator.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.cache.CacheOperation
import com.vauthenticator.server.cache.RedisCacheOperation
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.role.PermissionValidator
import com.vauthenticator.server.role.RoleCacheContentConverter
import com.vauthenticator.server.role.repository.CachedRoleRepository
import com.vauthenticator.server.role.repository.dynamodb.DynamoDbRoleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Duration

@Configuration(proxyBeanMethods = false)
class PermissionConfig {

    @Bean("roleRepository")
    @ConditionalOnProperty(
        name = ["vauthenticator.dynamo-db.role.cache.enabled"],
        havingValue = "false",
        matchIfMissing = true
    )
    fun roleRepository(
        mapper: ObjectMapper,
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.role.table-name}") roleTableName: String,
        @Value("\${vauthenticator.dynamo-db.role.protected-from-delete}") protectedRoleFromDeletion: List<String>
    ) = DynamoDbRoleRepository(protectedRoleFromDeletion, dynamoDbClient, roleTableName)


    @Bean("roleRepository")
    @ConditionalOnProperty(
        name = ["vauthenticator.dynamo-db.role.cache.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun cachedRoleRepository(
        mapper: ObjectMapper,
        roleCacheOperation: CacheOperation<String, String>,
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.role.table-name}") roleTableName: String,
        @Value("\${vauthenticator.dynamo-db.role.protected-from-delete}") protectedRoleFromDeletion: List<String>
    ) = CachedRoleRepository(
        RoleCacheContentConverter(mapper),
        roleCacheOperation,
        DynamoDbRoleRepository(protectedRoleFromDeletion, dynamoDbClient, roleTableName)
    )

    @Bean
    @ConditionalOnProperty(
        name = ["vauthenticator.dynamo-db.role.cache.enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    fun roleCacheOperation(
        redisTemplate: RedisTemplate<*, *>,
        @Value("\${vauthenticator.dynamo-db.role.cache.ttl}") ttl: Duration,
        @Value("\${vauthenticator.dynamo-db.role.cache.name}") cacheRegionName: String,
    ) = RedisCacheOperation<String, String>(
        cacheName = cacheRegionName,
        ttl = ttl,
        redisTemplate = redisTemplate as RedisTemplate<String, String>
    )

    @Bean
    fun permissionValidator(clientApplicationRepository: ClientApplicationRepository) =
        PermissionValidator(clientApplicationRepository)
}