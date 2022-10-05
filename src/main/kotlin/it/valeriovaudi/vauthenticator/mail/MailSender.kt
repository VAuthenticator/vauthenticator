package it.valeriovaudi.vauthenticator.mail

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplication
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import javax.mail.internet.MimeMessage

private const val MAIL_DOCUMENT_TYPE = "mail"

interface MailSenderService {
    fun send(mail: MailMessage)
    fun sendFor(account: Account, clientApplication: ClientApplication, requestContext: MailContext = emptyMap())
}

interface MailMessageFactory {
    fun makeMailMessageFor(account: Account, clientApplication: ClientApplication, requestContext: MailContext): MailMessage
}

class SimpleMailMessageFactory(val from: String, val subject: String, val mailType: MailType) : MailMessageFactory {

    override fun makeMailMessageFor(account: Account, clientApplication: ClientApplication, requestContext: MailContext): MailMessage {
        val context = mapOf(
                "enabled" to account.enabled,
                "username" to account.username,
                "authorities" to account.authorities,
                "email" to account.email,
                "firstName" to account.firstName,
                "lastName" to account.lastName,
                "birthDate" to account.birthDate.iso8601FormattedDate(),
                "phone" to account.phone.formattedPhone(),
        ) + requestContext
        return MailMessage(account.email, from, subject, mailType, context)
    }

}

class JavaMailSenderService(private val documentRepository: DocumentRepository,
                            private val mailSender: JavaMailSender,
                            private val templateResolver: MailTemplateResolver,
                            private val mailMessageFactory: MailMessageFactory) : MailSenderService {

    override fun send(mailMessage: MailMessage) {
        val mailContent = mailContentFor(mailMessage)
        val mail = composeMailFor(mailContent, mailMessage)
        mailSender.send(mail)
    }

    override fun sendFor(account: Account, clientApplication: ClientApplication, requestContext: MailContext) {
        val mailMessage = mailMessageFactory.makeMailMessageFor(account, clientApplication, requestContext)
        val mailContent = mailContentFor(mailMessage)
        val mail = composeMailFor(mailContent, mailMessage)
        mailSender.send(mail)
    }

    private fun mailContentFor(mail: MailMessage): String {
        val documentContent = documentRepository.loadDocument(MAIL_DOCUMENT_TYPE, mailTemplatePathFor(mail.type))
        return String(documentContent)
    }

    private fun mailTemplatePathFor(mailType: MailType): String =
            when (mailType) {
                MailType.WELCOME -> mailType.path
                else -> throw UnsupportedOperationException("mail tpe not supported")
            }

    private fun composeMailFor(mailContent: String, mailMessage: MailMessage): MimeMessage {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()

        val helper = MimeMessageHelper(mimeMessage, "utf-8")
        helper.setText(templateResolver.compile(mailContent, mailMessage.context), true) // Use this or above line.
        helper.setTo(mailMessage.to)
        helper.setSubject(mailMessage.subject)
        helper.setFrom(mailMessage.from)

        return mimeMessage
    }

}