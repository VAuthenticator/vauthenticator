package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.keypair.DynamoKeyRepository
import it.valeriovaudi.vauthenticator.keypair.KeyPairConfig
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.keypair.KmsKeyRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.kms.KmsClient

@Configuration(proxyBeanMethods = false)
class KeyConfig {
    @Bean
    @ConfigurationProperties(prefix = "key-store")
    fun keyPairConfig() = KeyPairConfig()

    @Bean
    fun keyRepository(kmsClient: KmsClient,
                      dynamoDbClient: DynamoDbClient,
                      @Value("\${vauthenticator.dynamo-db.keys.table-name}") tableName: String): KeyRepository =
            DynamoKeyRepository(
                    tableName,
                    KmsKeyRepository(kmsClient),
                    kmsClient,
                    dynamoDbClient
            )
}