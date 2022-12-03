package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.keypair.DynamoKeyRepository
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.keypair.KmsKeyRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.kms.KmsClient
import java.util.*

@Configuration(proxyBeanMethods = false)
class KeyConfig {

    @Bean
    fun keyRepository(kmsClient: KmsClient,
                      dynamoDbClient: DynamoDbClient,
                      @Value("\${vauthenticator.dynamo-db.keys.table-name}") tableName: String): KeyRepository =
            DynamoKeyRepository(
                    {UUID.randomUUID().toString()},
                    tableName,
                    KmsKeyRepository(kmsClient),
                    dynamoDbClient
            )
}