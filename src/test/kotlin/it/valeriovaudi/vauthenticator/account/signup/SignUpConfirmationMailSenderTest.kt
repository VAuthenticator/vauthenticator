package it.valeriovaudi.vauthenticator.account.signup

import com.hubspot.jinjava.Jinjava
import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import it.valeriovaudi.vauthenticator.mail.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.springframework.mail.javamail.JavaMailSenderImpl

internal class SignUpConfirmationMailSenderTest {
    @RegisterExtension
    val greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "pwd"))
            .withPerMethodLifecycle(true)


    lateinit var mailSenderService: MailSenderService
    lateinit var sender: SignUpConfirmationMailSender

    private val documentRepository: DocumentRepository = Mockito.mock(DocumentRepository::class.java)
    private val templateResolver: MailTemplateResolver = JinjavaMailTemplateResolver(Jinjava())

    @BeforeEach
    fun setUp() {
        val javaMailSender = JavaMailSenderImpl()
        javaMailSender.port = greenMail.smtp.port
        javaMailSender.host = "127.0.0.1"
        javaMailSender.username = "user"
        mailSenderService = JavaMailSenderService(documentRepository, javaMailSender, templateResolver)
        sender = SignUpConfirmationMailSender(mailSenderService,SimpleMailContextFactory(), SignUpConfirmationMailConfiguration("mail@mail.com", "test"))
    }

    @Test
    internal fun `when a mail is sent`() {
        val account = anAccount().copy(firstName = "Jhon", lastName = "Miller")
        val mailTemplateContent = "hi {{ firstName }} {{ lastName }} your signup to vauthenticator succeeded".toByteArray()

        BDDMockito.given(documentRepository.loadDocument("mail", MailType.SIGN_UP.path))
                .willReturn(mailTemplateContent)


        sender.sendConfirmation(account)
        val mail = greenMail.receivedMessages[0]
        assertEquals(account.email, mail.allRecipients[0].toString())
        assertEquals("mail@mail.com", mail.from[0].toString())
        assertEquals("test", mail.subject)
        assertEquals("hi Jhon Miller your signup to vauthenticator succeeded", mail.content.toString().trim())
    }

}