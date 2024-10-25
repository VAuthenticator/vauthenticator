package com.vauthenticator.server.config

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.password.adapter.dynamodb.DynamoPasswordHistoryRepository
import com.vauthenticator.server.password.domain.*
import com.vauthenticator.server.password.domain.changepassword.ChangePassword
import com.vauthenticator.server.password.domain.changepassword.ChangePasswordEventConsumer
import com.vauthenticator.server.password.domain.resetpassword.ResetPasswordEventConsumer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Clock

@EnableConfigurationProperties(PasswordGeneratorCriteria::class, PasswordPolicyConfigProp::class)
@Configuration(proxyBeanMethods = false)
class PasswordConfig {

    @Bean
    fun changePasswordEventConsumer(passwordHistoryRepository: PasswordHistoryRepository) =
        ChangePasswordEventConsumer(passwordHistoryRepository)

    @Bean
    fun resetPasswordEventConsumer(passwordHistoryRepository: PasswordHistoryRepository) =
        ResetPasswordEventConsumer(passwordHistoryRepository)

    @Bean
    fun passwordPolicy(
        reusePreventionPasswordPolicy: PasswordPolicy,
        passwordPolicyConfigProp: PasswordPolicyConfigProp
    ): CompositePasswordPolicy {
        val passwordPolicies = mutableSetOf(
            MinimumCharacterPasswordPolicy(passwordPolicyConfigProp.minSize),
            SpecialCharacterPasswordPolicy(passwordPolicyConfigProp.minSpecialSymbol)
        )
        if (passwordPolicyConfigProp.enablePasswordReusePrevention) {
            passwordPolicies.add(reusePreventionPasswordPolicy)
        }

        return CompositePasswordPolicy(
            passwordPolicies
        )
    }

    @Bean
    fun reusePreventionPasswordPolicy(
        passwordEncoder: VAuthenticatorPasswordEncoder,
        passwordHistoryRepository: PasswordHistoryRepository
    ) = ReusePreventionPasswordPolicy(
        passwordEncoder,
        passwordHistoryRepository
    )


    @Bean
    fun passwordHistoryRepository(
        @Value("\${vauthenticator.dynamo-db.password-history.history-evaluation-limit}") historyEvaluationLimit: Int,
        @Value("\${vauthenticator.dynamo-db.password-history.max-history-allowed-size}") maxHistoryAllowedSize: Int,
        @Value("\${vauthenticator.dynamo-db.password-history.table-name}") dynamoPasswordHistoryTableName: String,
        dynamoDbClient: DynamoDbClient
    ): DynamoPasswordHistoryRepository = DynamoPasswordHistoryRepository(
        historyEvaluationLimit,
        maxHistoryAllowedSize,
        Clock.systemUTC(),
        dynamoPasswordHistoryTableName,
        dynamoDbClient
    )

    @Bean
    fun changePassword(
        eventsDispatcher: VAuthenticatorEventsDispatcher,
        passwordPolicy: PasswordPolicy,
        passwordEncoder: VAuthenticatorPasswordEncoder,
        accountRepository: AccountRepository
    ) =
        ChangePassword(eventsDispatcher, passwordPolicy, passwordEncoder, accountRepository)

    @Bean
    fun passwordGenerator(passwordGeneratorCriteria: PasswordGeneratorCriteria) =
        PasswordGenerator(passwordGeneratorCriteria)
}

@ConfigurationProperties(prefix = "password.policy")
data class PasswordPolicyConfigProp(
    val minSize: Int,
    val minSpecialSymbol: Int,
    val enablePasswordReusePrevention: Boolean
)