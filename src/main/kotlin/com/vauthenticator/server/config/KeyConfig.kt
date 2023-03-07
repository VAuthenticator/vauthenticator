package com.vauthenticator.server.config

import com.vauthenticator.server.keys.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.kms.KmsClient
import java.util.*

@Configuration(proxyBeanMethods = false)
class KeyConfig {

    @Bean("keyGenerator")
    @ConditionalOnProperty(prefix = "platform.type", havingValue = "aws", matchIfMissing = false)
    fun awsKeyGenerator(kmsClient: KmsClient): KeyGenerator = KmsKeyGenerator(kmsClient)

    @Bean("keyDecrypter")
    @ConditionalOnProperty(prefix = "platform.type", havingValue = "aws", matchIfMissing = false)
    fun awsKeyDecrypter(kmsClient: KmsClient): KeyDecrypter = KmsKeyDecrypter(kmsClient)

    @Bean("keyGenerator")
    @ConditionalOnProperty(prefix = "platform.type", havingValue = "on-premise", matchIfMissing = false)
    fun onPremiseKeyGenerator(@Value("\${platform.one-premise.symmetric-key}") symmetricKey: String): KeyGenerator =
        OnPremiseKeyGenerator(symmetricKey)

    @Bean("keyDecrypter")
    @ConditionalOnProperty(prefix = "platform.type", havingValue = "on-premise", matchIfMissing = false)
    fun onPremiseKeyDecrypter(@Value("\${platform.one-premise.symmetric-key}") symmetricKey: String): KeyDecrypter =
        OnPremiseKeyDecrypter(symmetricKey)

    @Bean
    fun keyRepository(
        keyGenerator: KeyGenerator,
        keyDecrypter: KeyDecrypter,
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.keys.signature.table-name}") signatureTableName: String,
        @Value("\${vauthenticator.dynamo-db.keys.mfa.table-name}") mfaTableName: String
    ): KeyRepository =
        AwsKeyRepository(
            { UUID.randomUUID().toString() },
            signatureTableName,
            mfaTableName,
            keyGenerator,
            dynamoDbClient
        )
}