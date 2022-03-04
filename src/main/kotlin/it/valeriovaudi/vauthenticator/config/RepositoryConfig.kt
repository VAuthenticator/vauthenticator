package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.repository.DynamoDbAccountRepository
import it.valeriovaudi.vauthenticator.keypair.DynamoKeyRepository
import it.valeriovaudi.vauthenticator.keypair.KeyPairConfig
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.keypair.KmsKeyRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoDbClientApplicationRepository
import it.valeriovaudi.vauthenticator.role.DynamoDbRoleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.kms.KmsClient


@Configuration(proxyBeanMethods = false)
class RepositoryConfig {

    @Bean
    @ConfigurationProperties(prefix = "key-store")
    fun keyPairConfig() = KeyPairConfig()

    @Bean
    fun kmsClient(awsCredentialsProvider: AwsCredentialsProvider) = KmsClient.builder()
            .credentialsProvider(awsCredentialsProvider)
            .build()

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

@Configuration
class DynamoDbRepositoryConfig {

    @Bean
    fun dynamoDbClient(awsCredentialsProvider: AwsCredentialsProvider) = DynamoDbClient.builder()
            .credentialsProvider(awsCredentialsProvider)
            .build()

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