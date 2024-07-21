package com.vauthenticator.server.mfa

import com.hubspot.jinjava.Jinjava
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.email.*
import com.vauthenticator.server.keys.KeyDecrypter
import com.vauthenticator.server.keys.KeyRepository
import com.vauthenticator.server.keys.MasterKid
import com.vauthenticator.server.mask.SensitiveEmailMasker
import com.vauthenticator.server.mfa.domain.*
import com.vauthenticator.server.mfa.repository.DynamoMfaAccountMethodsRepository
import com.vauthenticator.server.mfa.repository.MfaAccountMethodsRepository
import com.vauthenticator.server.ticket.TicketCreator
import com.vauthenticator.server.ticket.TicketRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
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
    fun sensitiveEmailMasker() = SensitiveEmailMasker()

    @Bean
    fun mfaMethodsEnrolmentAssociation(
        ticketRepository: TicketRepository,
        mfaAccountMethodsRepository: MfaAccountMethodsRepository
    ) =
        MfaMethodsEnrollmentAssociation(ticketRepository, mfaAccountMethodsRepository)

    @Bean
    fun mfaMethodsEnrollment(
        mfaSender : OtpMfaSender,
        ticketCreator: TicketCreator,
    ) = MfaMethodsEnrollment(ticketCreator, mfaSender)

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
        mfaMailSender: EMailSenderService
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
        noReplyEMailConfiguration: NoReplyEMailConfiguration
    ) =
        JavaEMailSenderService(
            documentRepository,
            javaMailSender,
            JinjavaMailTemplateResolver(Jinjava()),
            SimpleEMailMessageFactory(
                noReplyEMailConfiguration.from,
                noReplyEMailConfiguration.mfaEMailSubject, // todo chenge the subject
                EMailType.MFA
            )
        )
}

@ConfigurationProperties("mfa.otp")
data class OtpConfigurationProperties(val length: Int, val timeToLiveInSeconds: Int)