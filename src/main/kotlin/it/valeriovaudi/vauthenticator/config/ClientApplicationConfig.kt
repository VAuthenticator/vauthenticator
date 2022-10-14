package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoDbClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ReadClientApplication
import it.valeriovaudi.vauthenticator.oauth2.clientapp.StoreClientApplication
import it.valeriovaudi.vauthenticator.security.VAuthenticatorPasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration(proxyBeanMethods = false)
class ClientApplicationConfig {

    @Bean
    fun clientApplicationRepository(dynamoDbClient: DynamoDbClient,
                                    passwordEncoder: PasswordEncoder,
                                    @Value("\${vauthenticator.dynamo-db.client-application.table-name}") clientAppTableName: String) =
            DynamoDbClientApplicationRepository(dynamoDbClient, clientAppTableName)

    @Bean
    fun readClientApplication(clientApplicationRepository: ClientApplicationRepository) =
            ReadClientApplication(clientApplicationRepository)

    @Bean
    fun storeClientApplication(clientApplicationRepository: ClientApplicationRepository,
                               passwordEncoder: VAuthenticatorPasswordEncoder) =
            StoreClientApplication(clientApplicationRepository, passwordEncoder)


}