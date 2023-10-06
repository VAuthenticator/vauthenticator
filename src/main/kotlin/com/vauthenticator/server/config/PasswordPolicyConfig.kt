package com.vauthenticator.server.config

import com.vauthenticator.server.password.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Clock

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PasswordPolicyConfigProp::class)
class PasswordPolicyConfig {

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
}