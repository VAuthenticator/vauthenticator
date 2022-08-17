package it.valeriovaudi.vauthenticator.mail

import it.valeriovaudi.vauthenticator.document.DocumentRepository
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import javax.mail.internet.MimeMessage

private const val MAIL_DOCUMENT_TYPE = "mail"

interface MailSenderService {
    fun send(mail: MailMessage)
}

class JavaMailSenderService(private val documentRepository: DocumentRepository,
                            private val mailSender: JavaMailSender,
                            private val templateResolver: MailTemplateResolver) : MailSenderService {

    override fun send(mailMessage: MailMessage) {
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
                MailType.SIGN_UP -> mailType.path
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