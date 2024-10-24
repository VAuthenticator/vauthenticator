package com.vauthenticator.server.config

import com.vauthenticator.server.keys.adapter.dynamo.DynamoDbKeyStorage
import com.vauthenticator.server.keys.adapter.jdbc.JdbcKeyStorage
import com.vauthenticator.server.keys.adapter.kms.KmsKeyDecrypter
import com.vauthenticator.server.keys.adapter.kms.KmsKeyGenerator
import com.vauthenticator.server.keys.domain.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.kms.KmsClient
import java.time.Clock
import java.util.*

@Configuration(proxyBeanMethods = false)
class KeyConfig {

    @Bean
    fun keyGenerator(kmsClient: KmsClient): KeyGenerator = KmsKeyGenerator(kmsClient)

    @Bean
    fun keyDecrypter(kmsClient: KmsClient): KeyDecrypter = KmsKeyDecrypter(kmsClient)

    @Bean("keyStorage")
    @Profile("!experimental_database_persistence")
    fun dynamoDbKeyStorage(
        clock: Clock,
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.keys.signature.table-name}") signatureTableName: String,
        @Value("\${vauthenticator.dynamo-db.keys.mfa.table-name}") mfaTableName: String
    ) = DynamoDbKeyStorage(clock, dynamoDbClient, signatureTableName, mfaTableName)

    @Bean("keyStorage")
    @Profile("experimental_database_persistence")
    fun jdbcKeyStorage(jdbcTemplate: JdbcTemplate, clock: Clock) = JdbcKeyStorage(jdbcTemplate, clock)

    @Bean("keyRepository")
    fun keyRepository(
        keyGenerator: KeyGenerator,
        keyDecrypter: KeyDecrypter,
        keyStorage: KeyStorage
    ): KeyRepository =
        KeyRepository(
            { UUID.randomUUID().toString() },
            keyStorage,
            keyGenerator,
        )

    @Bean
    fun signatureKeyRotation(keyRepository: KeyRepository) = SignatureKeyRotation(keyRepository)
}