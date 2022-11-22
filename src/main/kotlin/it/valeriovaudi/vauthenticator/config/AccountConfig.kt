package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.account.repository.DynamoDbAccountRepository
import it.valeriovaudi.vauthenticator.account.repository.EnrichedAccountRepository
import it.valeriovaudi.vauthenticator.password.CompositePasswordPolicy
import it.valeriovaudi.vauthenticator.password.MinimumCharacterPasswordPolicy
import it.valeriovaudi.vauthenticator.password.PasswordPolicy
import it.valeriovaudi.vauthenticator.password.SpecialCharacterPasswordPolicy
import it.valeriovaudi.vauthenticator.role.DynamoDbRoleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration(proxyBeanMethods = false)
class AccountConfig {

    @Bean
    fun accountRepository(
        passwordPolicy: PasswordPolicy,
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.account.table-name}") accountTableName: String,
        @Value("\${vauthenticator.dynamo-db.account.role.table-name}") accountRoleTableName: String
    ) = EnrichedAccountRepository(
        DynamoDbAccountRepository(dynamoDbClient, accountTableName, accountRoleTableName),
        passwordPolicy
    )

    @Bean
    fun roleRepository(
        dynamoDbClient: DynamoDbClient,
        @Value("\${vauthenticator.dynamo-db.role.table-name}") roleTableName: String
    ) =
        DynamoDbRoleRepository(dynamoDbClient, roleTableName)

}