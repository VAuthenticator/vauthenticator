package com.vauthenticator.server.email

import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.document.repository.DocumentType
import com.vauthenticator.server.account.Account
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper


interface EMailSenderService {
    fun sendFor(account: Account, eMailContext: EMailContext = emptyMap())
}

fun interface EMailMessageFactory {
    fun makeMailMessageFor(account: Account, requestContext: EMailContext): EMailMessage
}

class SimpleEMailMessageFactory(val from: String, val subject: String, private val eMailType: EMailType) : EMailMessageFactory {

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
        return EMailMessage(account.email, from, subject, eMailType, context)
    }

}

class JavaEMailSenderService(
    private val documentRepository: DocumentRepository,
    private val mailSender: JavaMailSender,
    private val templateResolver: MailTemplateResolver,
    private val EMailMessageFactory: EMailMessageFactory
) : EMailSenderService {

    override fun sendFor(account: Account, EMailContext: EMailContext) {
        val mailMessage = EMailMessageFactory.makeMailMessageFor(account, EMailContext)
        val mailContent = mailContentFor(mailMessage)
        val email = composeMailFor(mailContent, mailMessage)
        mailSender.send(email)
    }

    private fun mailContentFor(email: EMailMessage): String {
        val documentContent = documentRepository.loadDocument(DocumentType.MAIL.content, mailTemplatePathFor(email.type))
        return String(documentContent.content)
    }

    private fun mailTemplatePathFor(EMailType: EMailType): String = EMailType.path

    private fun composeMailFor(mailContent: String, EMailMessage: EMailMessage): MimeMessage {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()

        val helper = MimeMessageHelper(mimeMessage, "utf-8")
        helper.setText(templateResolver.compile(mailContent, EMailMessage.context), true) // Use this or above line.
        helper.setTo(EMailMessage.to)
        helper.setSubject(EMailMessage.subject)
        helper.setFrom(EMailMessage.from)

        return mimeMessage
    }

}