package com.vauthenticator.server.mail

import com.vauthenticator.document.repository.DocumentRepository
import com.vauthenticator.server.account.Account
import com.vauthenticator.server.document.DocumentType
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper


interface MailSenderService {
    fun sendFor(account: Account, mailContext: MailContext = emptyMap())
}

interface MailMessageFactory {
    fun makeMailMessageFor(account: Account, requestContext: MailContext): MailMessage
}

class SimpleMailMessageFactory(val from: String, val subject: String, val mailType: MailType) : MailMessageFactory {

    override fun makeMailMessageFor(account: Account, requestContext: MailContext): MailMessage {
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
        return MailMessage(account.email, from, subject, mailType, context)
    }

}

class JavaMailSenderService(
    private val documentRepository: DocumentRepository,
    private val mailSender: JavaMailSender,
    private val templateResolver: MailTemplateResolver,
    private val mailMessageFactory: MailMessageFactory
) : MailSenderService {

    override fun sendFor(account: Account, mailContext: MailContext) {
        val mailMessage = mailMessageFactory.makeMailMessageFor(account, mailContext)
        val mailContent = mailContentFor(mailMessage)
        val mail = composeMailFor(mailContent, mailMessage)
        mailSender.send(mail)
    }

    private fun mailContentFor(mail: MailMessage): String {
        val documentContent = documentRepository.loadDocument(DocumentType.MAIL.content, mailTemplatePathFor(mail.type))
        return String(documentContent.content)
    }

    private fun mailTemplatePathFor(mailType: MailType): String = mailType.path

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