package com.vauthenticator.config

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.account.repository.AccountRepository
import com.vauthenticator.document.DocumentRepository
import com.vauthenticator.keys.KeyDecrypter
import com.vauthenticator.keys.KeyRepository
import com.vauthenticator.keys.MasterKid
import com.vauthenticator.mail.*
import com.vauthenticator.mfa.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration(proxyBeanMethods = false)
class MfaConfig {

    @Bean
    fun mfaAccountMethodsRepository(
        keyRepository: KeyRepository,
        dynamoDbClient: DynamoDbClient,
        @Value("\${key.master-key}") masterKey: String,
        @Value("\${vauthenticator.dynamo-db.mfa-account-methods.table-name}") tableName: String
    ): MfaAccountMethodsRepository =
        DynamoMfaAccountMethodsRepository(
            tableName,
            dynamoDbClient,
            keyRepository,
            MasterKid(masterKey)
        )

    @Bean
    fun mfaMethodsEnrolmentAssociation(mfaAccountMethodsRepository: MfaAccountMethodsRepository) =
        MfaMethodsEnrolmentAssociation(mfaAccountMethodsRepository)

    @Bean
    fun otpMfa(
        keyRepository: KeyRepository,
        keyDecrypter: KeyDecrypter,
        mfaAccountMethodsRepository: MfaAccountMethodsRepository,
        otpConfigurationProperties: OtpConfigurationProperties
    ) = TaimosOtpMfa(
        keyDecrypter,
        keyRepository,
        mfaAccountMethodsRepository,
        otpConfigurationProperties
    )

    @Bean
    fun otpMfaSender(
        accountRepository: AccountRepository,
        otpMfa: OtpMfa,
        mfaMailSender: MailSenderService
    ) = OtpMfaEmailSender(accountRepository, otpMfa, mfaMailSender)

    @Bean
    fun otpMfaVerifier(
        accountRepository: AccountRepository,
        otpMfa: OtpMfa
    ) = AccountAwareOtpMfaVerifier(accountRepository, otpMfa)

    @Bean
    fun mfaMailSender(
        javaMailSender: JavaMailSender,
        documentRepository: DocumentRepository,
        noReplyMailConfiguration: NoReplyMailConfiguration
    ) =
        JavaMailSenderService(
            documentRepository,
            javaMailSender,
            JinjavaMailTemplateResolver(Jinjava()),
            SimpleMailMessageFactory(
                noReplyMailConfiguration.from,
                noReplyMailConfiguration.mfaMailSubject, // todo chenge the subject
                MailType.MFA
            )
        )
}