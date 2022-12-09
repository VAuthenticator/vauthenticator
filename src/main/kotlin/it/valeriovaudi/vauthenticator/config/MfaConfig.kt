package it.valeriovaudi.vauthenticator.config

import com.hubspot.jinjava.Jinjava
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import it.valeriovaudi.vauthenticator.keys.KeyRepository
import it.valeriovaudi.vauthenticator.keys.MasterKid
import it.valeriovaudi.vauthenticator.mail.*
import it.valeriovaudi.vauthenticator.mfa.*
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
        @Value("\${vauthenticator.dynamo-db.mfa_account_methods.table-name}") tableName: String
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
        mfaAccountMethodsRepository: MfaAccountMethodsRepository,
        otpConfigurationProperties: OtpConfigurationProperties
    ) = TaimosOtpMfa(
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