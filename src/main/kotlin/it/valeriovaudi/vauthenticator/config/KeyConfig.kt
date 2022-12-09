package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.keys.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.kms.KmsClient
import java.util.*

@Configuration(proxyBeanMethods = false)
class KeyConfig {

    @Bean
    fun keyGenerator(kmsClient: KmsClient): KeyGenerator = KmsKeyRepository(kmsClient)

    @Bean
    fun keyDecrypter(kmsClient: KmsClient): KeyDecrypter = KmsKeyRepository(kmsClient)

    @Bean
    fun keyRepository(
        keyGenerator: KeyGenerator,
        keyDecrypter: KeyDecrypter,
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.keys.table-name}") tableName: String
    ): KeyRepository =
        AwsKeyRepository(
            { UUID.randomUUID().toString() },
            tableName,
            keyGenerator,
            dynamoDbClient
        )
}