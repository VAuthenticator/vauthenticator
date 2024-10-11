package com.vauthenticator.server.config

import com.vauthenticator.server.keys.adapter.dynamo.DynamoDbKeyStorage
import com.vauthenticator.server.keys.adapter.kms.KmsKeyDecrypter
import com.vauthenticator.server.keys.adapter.kms.KmsKeyGenerator
import com.vauthenticator.server.keys.domain.KeyDecrypter
import com.vauthenticator.server.keys.domain.KeyGenerator
import com.vauthenticator.server.keys.domain.KeyRepository
import com.vauthenticator.server.keys.domain.SignatureKeyRotation
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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

    @Bean
    fun keyRepository(
        clock: Clock,
        keyGenerator: KeyGenerator,
        keyDecrypter: KeyDecrypter,
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.keys.signature.table-name}") signatureTableName: String,
        @Value("\${vauthenticator.dynamo-db.keys.mfa.table-name}") mfaTableName: String
    ): KeyRepository =
        KeyRepository(
            { UUID.randomUUID().toString() },
            DynamoDbKeyStorage(
                clock,
                dynamoDbClient,
                signatureTableName,
                mfaTableName,
            ),
            keyGenerator,
        )


    @Bean
    fun signatureKeyRotation(keyRepository: KeyRepository) = SignatureKeyRotation(keyRepository)
}