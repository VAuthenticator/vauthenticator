package com.vauthenticator.server.communication.email

import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.document.repository.DocumentType
import com.vauthenticator.server.account.Account
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper


fun interface EMailSenderService {
    fun sendFor(account: Account, emailContext: EMailContext)
}

fun interface EMailMessageFactory {
    fun makeMailMessageFor(account: Account, requestContext: EMailContext): EMailMessage
}

class SimpleEMailMessageFactory(val from: String, val subject: String, private val emailType: EMailType) :
    EMailMessageFactory {

    override fun makeMailMessageFor(account: Account, requestContext: EMailContext): EMailMessage {
        val context = mapOf(
            "enabled" to account.enabled,
            "username" to account.username,
            "authorities" to account.authorities,
            "email" to account.email,
            "firstName" to account.firstName,
            "lastName" to account.lastName,
            "birthDate" to account.birthDate.map { it.iso8601FormattedDate() }.orElse(""),
            "phone" to account.phone.map { it.formattedPhone() }.orElse("")
        ) + requestContext
        return EMailMessage(context["email"] as String, from, subject, emailType, context)
    }

}

class JavaEMailSenderService(
    private val documentRepository: DocumentRepository,
    private val mailSender: JavaMailSender,
    private val templateResolver: MailTemplateResolver,
    private val emailMessageFactory: EMailMessageFactory
) : EMailSenderService {

    override fun sendFor(account: Account, emailContext: EMailContext) {
        val mailMessage = emailMessageFactory.makeMailMessageFor(account, emailContext)
        val mailContent = mailContentFor(mailMessage)
        val email = composeMailFor(mailContent, mailMessage)
        mailSender.send(email)
    }

    private fun mailContentFor(email: EMailMessage): String {
        val documentContent = documentRepository.loadDocument(DocumentType.MAIL.content, mailTemplatePathFor(email.type))
        return String(documentContent.content)
    }

    private fun mailTemplatePathFor(emailType: EMailType): String = emailType.path

    private fun composeMailFor(mailContent: String, emailMessage: EMailMessage): MimeMessage {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()

        val helper = MimeMessageHelper(mimeMessage, "utf-8")
        helper.setText(templateResolver.compile(mailContent, emailMessage.context), true) // Use this or above line.
        helper.setTo(emailMessage.to)
        helper.setSubject(emailMessage.subject)
        helper.setFrom(emailMessage.from)

        return mimeMessage
    }

}