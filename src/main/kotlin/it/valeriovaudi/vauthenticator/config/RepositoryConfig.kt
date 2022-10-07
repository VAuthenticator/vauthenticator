package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.repository.DynamoDbAccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.DynamoDbTicketRepository
import it.valeriovaudi.vauthenticator.document.S3DocumentRepository
import it.valeriovaudi.vauthenticator.keypair.DynamoKeyRepository
import it.valeriovaudi.vauthenticator.keypair.KeyPairConfig
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.keypair.KmsKeyRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoDbClientApplicationRepository
import it.valeriovaudi.vauthenticator.role.DynamoDbRoleRepository
import it.valeriovaudi.vauthenticator.time.UtcClocker
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.s3.S3Client

@Configuration(proxyBeanMethods = false)
class DocumentRepositoryConfig {

    @Bean
    fun documentRepository(@Value("\${document.bucket-name}") documentBucketName: String, s3Client: S3Client) =
            S3DocumentRepository(s3Client, documentBucketName)

}

@Configuration(proxyBeanMethods = false)
class KeyRepositoryConfig {

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
                    dynamoDbClient
            )

}

@Configuration(proxyBeanMethods = false)
class DynamoDbRepositoryConfig {

    @Bean
    fun accountRepository(dynamoDbClient: DynamoDbClient,
                          @Value("\${vauthenticator.dynamo-db.account.table-name}") accountTableName: String,
                          @Value("\${vauthenticator.dynamo-db.account.role.table-name}") accountRoleTableName: String
    ) = DynamoDbAccountRepository(dynamoDbClient, accountTableName, accountRoleTableName)

    @Bean
    fun roleRepository(dynamoDbClient: DynamoDbClient,
                       @Value("\${vauthenticator.dynamo-db.role.table-name}") roleTableName: String) =
            DynamoDbRoleRepository(dynamoDbClient, roleTableName)

    @Bean
    fun clientApplicationRepository(dynamoDbClient: DynamoDbClient,
                                    passwordEncoder: PasswordEncoder,
                                    @Value("\${vauthenticator.dynamo-db.client-application.table-name}") clientAppTableName: String) =
            DynamoDbClientApplicationRepository(dynamoDbClient, clientAppTableName)
}

@Configuration(proxyBeanMethods = false)
class DynamoDbTicketRepositoryConfig {

    @Bean
    fun ticketRepository(@Value("\${vauthenticator.dynamo-db.ticket.table-name}") tableName: String, dynamoDbClient: DynamoDbClient) =
            DynamoDbTicketRepository(dynamoDbClient, UtcClocker(), tableName)


}