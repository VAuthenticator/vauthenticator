package it.valeriovaudi.vauthenticator.config

import com.hubspot.jinjava.Jinjava
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import it.valeriovaudi.vauthenticator.mail.*
import it.valeriovaudi.vauthenticator.mfa.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender

@Configuration(proxyBeanMethods = false)
class MfaConfig {

    @Bean
    fun mfaMethodsEnrolmentAssociation() = MfaMethodsEnrolmentAssociation()

    @Bean
    fun otpMfa(otpConfigurationProperties: OtpConfigurationProperties) = TaimosOtpMfa(otpConfigurationProperties)

    @Bean
    fun otpMfaSender(
        accountRepository: AccountRepository,
        otpMfa: OtpMfa,
        mfaMailSender: MailSenderService
    ) = OtpMfaEmailSender(accountRepository, otpMfa, mfaMailSender)

    @Bean
    fun aotpMfaVerifier(
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