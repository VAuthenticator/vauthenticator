package com.vauthenticator.server.email

import com.hubspot.jinjava.Jinjava
import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.mail.javamail.JavaMailSenderImpl

internal class JavaMailSenderServiceTest {
    @RegisterExtension
    val greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP)
        .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "pwd"))
        .withPerMethodLifecycle(true)


    private val mail = "mail@mail.com"
    private val subject = "test"

    private lateinit var mailSenderService: MailSenderService
    private val documentRepository: DocumentRepository = mockk()

    private val templateResolver: MailTemplateResolver = JinjavaMailTemplateResolver(Jinjava())

    private fun newJavaMail(): JavaMailSenderImpl {
        val javaMailSender = JavaMailSenderImpl()
        javaMailSender.port = greenMail.smtp.port
        javaMailSender.host = "127.0.0.1"
        javaMailSender.username = "user"
        return javaMailSender
    }

    private fun simpleMailMessageFactoryFor(mailType: MailType) = SimpleMailMessageFactory(mail, subject, mailType)

    @Test
    internal fun `when a welcome mail is sent`() {
        val account = anAccount().copy(firstName = "Jhon", lastName = "Miller")
        val mailTemplateContent =
            "hi {{ firstName }} {{ lastName }} your signup to vauthenticator succeeded".toByteArray()

        mailSenderService = JavaMailSenderService(
            documentRepository, newJavaMail(), templateResolver, simpleMailMessageFactoryFor(
                MailType.WELCOME
            )
        )

        every { documentRepository.loadDocument("mail", MailType.WELCOME.path).content } returns mailTemplateContent


        mailSenderService.sendFor(account)
        val mail = greenMail.receivedMessages[0]
        assertEquals(account.email, mail.allRecipients[0].toString())
        assertEquals(this.mail, mail.from[0].toString())
        assertEquals(subject, mail.subject)
        assertEquals("hi Jhon Miller your signup to vauthenticator succeeded", mail.content.toString().trim())
    }


    @Test
    internal fun `when a mail verification mail is sent`() {
        val account = anAccount().copy(firstName = "Jhon", lastName = "Miller")
        val mailTemplateContent =
            "hi {{ firstName }} {{ lastName }} in order to verify your mail click <a href='{{ mailVerificationTicket }}'>here</a>".toByteArray()

        mailSenderService = JavaMailSenderService(
            documentRepository, newJavaMail(), templateResolver, simpleMailMessageFactoryFor(
                MailType.EMAIL_VERIFICATION
            )
        )

        every { documentRepository.loadDocument("mail", MailType.EMAIL_VERIFICATION.path).content } returns mailTemplateContent

        mailSenderService.sendFor(
            account,
            mapOf("mailVerificationTicket" to "https://vauthenticator.com/mail-verify/uuid-1234-uuid")
        )
        val mail = greenMail.receivedMessages[0]
        assertEquals(account.email, mail.allRecipients[0].toString())
        assertEquals(this.mail, mail.from[0].toString())
        assertEquals(subject, mail.subject)
        assertEquals(
            "hi Jhon Miller in order to verify your mail click <a href='https://vauthenticator.com/mail-verify/uuid-1234-uuid'>here</a>",
            mail.content.toString().trim()
        )
    }

    @Test
    internal fun `when a reset password mail is sent`() {
        val account = anAccount().copy(firstName = "Jhon", lastName = "Miller")
        val mailTemplateContent =
            "hi {{ firstName }} {{ lastName }} in order to reset your password click <a href='{{ resetPasswordLink }}'>here</a>".toByteArray()

        mailSenderService = JavaMailSenderService(
            documentRepository, newJavaMail(), templateResolver, simpleMailMessageFactoryFor(
                MailType.RESET_PASSWORD
            )
        )

        every { documentRepository.loadDocument("mail", MailType.RESET_PASSWORD.path).content } returns mailTemplateContent

        mailSenderService.sendFor(
            account,
            mapOf("resetPasswordLink" to "https://vauthenticator.com/reset-password/uuid-1234-uuid")
        )
        val mail = greenMail.receivedMessages[0]
        assertEquals(account.email, mail.allRecipients[0].toString())
        assertEquals(this.mail, mail.from[0].toString())
        assertEquals(subject, mail.subject)
        assertEquals(
            "hi Jhon Miller in order to reset your password click <a href='https://vauthenticator.com/reset-password/uuid-1234-uuid'>here</a>",
            mail.content.toString().trim()
        )
    }
}