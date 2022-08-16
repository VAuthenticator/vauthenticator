package it.valeriovaudi.vauthenticator.account.signup

import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import it.valeriovaudi.vauthenticator.mail.JavaMailSenderService
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mail.javamail.JavaMailSenderImpl

@ExtendWith(MockitoExtension::class)
internal class SignUpConfirmationMailSenderTest {

    @RegisterExtension
    val greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "pwd"))
            .withPerMethodLifecycle(true)

    lateinit var mailSenderService: MailSenderService

    @Mock
    lateinit var documentRepository: DocumentRepository

    @BeforeEach
    fun setUp() {
        val javaMailSender = JavaMailSenderImpl()
        javaMailSender.port = greenMail.smtp.port
        javaMailSender.host = "127.0.0.1"
        javaMailSender.username = "user"
        mailSenderService = JavaMailSenderService(documentRepository, javaMailSender)
    }

    @Test
    internal fun `when a confirmation mail is sent`() {
        given(documentRepository.loadDocument("mail", "templates/sign-up.html"))
                .willReturn("hi %s your signup to vauthenticator succeeded".toByteArray())

        val underTest = SignUpConfirmationMailSender(mailSenderService, SignUpConfirmationMailConfiguration("mail@mail.com", "signup to vauthenticator succeeded"))
        underTest.sendConfirmation(anAccount())

        val mail = greenMail.receivedMessages[0]

        Assertions.assertEquals("email@domain.com", mail.allRecipients[0].toString())
        Assertions.assertEquals("mail@mail.com", mail.from[0].toString())
        Assertions.assertEquals("signup to vauthenticator succeeded", mail.subject)
        Assertions.assertEquals("hi A First Name your signup to vauthenticator succeeded", mail.content.toString().trim())

    }

}

