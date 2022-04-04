package it.valeriovaudi.vauthenticator.mail

import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.mail.javamail.JavaMailSenderImpl

internal class JavaMailSenderServiceTest {

    @RegisterExtension
    val greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "pwd"))
            .withPerMethodLifecycle(true)


    lateinit var mailSenderService: MailSenderService

    @BeforeEach
    fun setUp(){
        val javaMailSender = JavaMailSenderImpl()
        javaMailSender.port = greenMail.smtp.port
        javaMailSender.host = "127.0.0.1"
        javaMailSender.username = "user"
        mailSenderService = JavaMailSenderService(javaMailSender)
    }

    @Test
    internal fun `when a mail is sent`() {
        mailSenderService.send(MailMessage("mail@mail.com","mail@mail.com", "test", "test message body"))
        val mail = greenMail.receivedMessages[0]
        Assertions.assertEquals("mail@mail.com", mail.allRecipients[0].toString())
        Assertions.assertEquals("mail@mail.com", mail.from[0].toString())
        Assertions.assertEquals("test", mail.subject)
        Assertions.assertEquals("test message body", mail.content.toString().trim())
    }

}