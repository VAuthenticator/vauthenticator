package it.valeriovaudi.vauthenticator.mail

import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.ServerSetupTest
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.mail.javamail.JavaMailSenderImpl

internal class JavaMailSenderServiceTest {
    @RegisterExtension
    val greenMail: GreenMailExtension = GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "pwd"))
            .withPerMethodLifecycle(true)


    lateinit var mailSenderService: MailSenderService

    private val documentRepository: DocumentRepository = mock(DocumentRepository::class.java)
    private val templateResolver: MailTemplateResolver = mock(MailTemplateResolver::class.java)

    @BeforeEach
    fun setUp() {
        val javaMailSender = JavaMailSenderImpl()
        javaMailSender.port = greenMail.smtp.port
        javaMailSender.host = "127.0.0.1"
        javaMailSender.username = "user"
        mailSenderService = JavaMailSenderService(documentRepository, javaMailSender, templateResolver)
    }

    @Test
    internal fun `when a mail is sent`() {
        val mailTemplateContent = "hi %s your signup to vauthenticator succeeded".toByteArray()
        val context = mapOf("firstName" to "A First Name")

        given(documentRepository.loadDocument("mail", MailType.SIGN_UP.path))
                .willReturn(mailTemplateContent)

        given(templateResolver.compile("hi %s your signup to vauthenticator succeeded", context))
                .willReturn("hi A First Name your signup to vauthenticator succeeded")

        mailSenderService.send(MailMessage("mail@mail.com", "mail@mail.com", "test", MailType.SIGN_UP, context))
        val mail = greenMail.receivedMessages[0]
        assertEquals("mail@mail.com", mail.allRecipients[0].toString())
        assertEquals("mail@mail.com", mail.from[0].toString())
        assertEquals("test", mail.subject)
        assertEquals("hi A First Name your signup to vauthenticator succeeded", mail.content.toString().trim())
    }
}