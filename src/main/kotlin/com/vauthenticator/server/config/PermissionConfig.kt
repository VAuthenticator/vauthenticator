package com.vauthenticator.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.cache.RedisCacheOperation
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.role.CachedRoleRepository
import com.vauthenticator.server.role.DynamoDbRoleRepository
import com.vauthenticator.server.role.PermissionValidator
import com.vauthenticator.server.role.RoleCacheContentConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Duration

@Configuration(proxyBeanMethods = false)
class PermissionConfig {

    @Bean
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
    fun roleRepository(
        mapper: ObjectMapper,
        roleCacheOperation: RedisCacheOperation<String, String>,
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.role.table-name}") roleTableName: String
    ) =
        CachedRoleRepository(
            RoleCacheContentConverter(mapper),
            roleCacheOperation,
            DynamoDbRoleRepository(dynamoDbClient, roleTableName)
        )

    @Bean
    fun permissionValidator(clientApplicationRepository: ClientApplicationRepository) =
        PermissionValidator(clientApplicationRepository)
}