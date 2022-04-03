package it.valeriovaudi.vauthenticator.account.signup

import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.mail.JavaMailSenderService
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.mail.javamail.JavaMailSenderImpl

internal class SignUpConfirmationMailSenderTest {

    @RegisterExtension
    val greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "pwd"))
            .withPerMethodLifecycle(true)

    lateinit var mailSenderService: MailSenderService

    @BeforeEach
    fun setUp() {
        val javaMailSender = JavaMailSenderImpl()
        javaMailSender.port = greenMail.smtp.port
        javaMailSender.host = "127.0.0.1"
        javaMailSender.username = "user"
        javaMailSender.password = "pwd"
        mailSenderService = JavaMailSenderService(javaMailSender)
    }

    @Test
    internal fun `when a confirmation mail is sent`() {
        val underTest = SignUpConfirmationMailSender(mailSenderService, SignUpConfirmationMailConfiguration(true, "mail@mail.com", "signup to vauthenticator succeeded", "hi %s your signup to vauthenticator succeeded"))
        underTest.sendConfirmation(anAccount())

        val mail = greenMail.receivedMessages[0]

        Assertions.assertEquals("email@domain.com", mail.allRecipients[0].toString())
        Assertions.assertEquals("mail@mail.com", mail.from[0].toString())
        Assertions.assertEquals("signup to vauthenticator succeeded", mail.subject)
        Assertions.assertEquals("hi A First Name your signup to vauthenticator succeeded", mail.content.toString().trim())


    }

    @Test
    internal fun `when a confirmation mail is disabled`() {
        val underTest = SignUpConfirmationMailSender(mailSenderService, SignUpConfirmationMailConfiguration(false, "mail@mail.com", "signup to vauthenticator succeeded", "hi %s your signup to vauthenticator succeeded"))
        underTest.sendConfirmation(anAccount())

        val mails = greenMail.receivedMessages.size

        Assertions.assertEquals(0, mails)

    }
}

