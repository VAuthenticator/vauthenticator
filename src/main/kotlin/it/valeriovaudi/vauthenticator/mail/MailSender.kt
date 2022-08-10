package it.valeriovaudi.vauthenticator.mail

import it.valeriovaudi.vauthenticator.document.DocumentRepository
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import javax.mail.internet.MimeMessage


data class MailMessage(val to: String, val from: String, val subject: String, val text: String, val type: MailType = MailType.SIGN_UP)

enum class MailType(val path: String) {
    SIGN_UP("templates/sign-up.html"), EMAIL_VERIFICATION(""), RESET_PASSWORD("");
}

interface MailSenderService {

    fun send(mail: MailMessage)
}

class JavaMailSenderService(private val javaMailSender: JavaMailSender) : MailSenderService {

    override fun send(mail: MailMessage) {
        javaMailSender.send(simpleMailMessage(mail))
    }

    private fun simpleMailMessage(mail: MailMessage): SimpleMailMessage {
        val simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.setTo(mail.to)
        simpleMailMessage.setFrom(mail.from)
        simpleMailMessage.setSubject(mail.subject)
        simpleMailMessage.setText(mail.text)
        return simpleMailMessage
    }

}

private const val MAIL_DOCUMENT_TYPE = "mail"

class TypedMailSenderService(private val documentRepository: DocumentRepository,
                             private val mailSender: JavaMailSender) : MailSenderService {

    override fun send(mail: MailMessage) {
        val documentContent = documentRepository.loadDocument(MAIL_DOCUMENT_TYPE, mailTemplatePathFor(mail.type))
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, "utf-8")
        val htmlMsg = String(documentContent)
        helper.setText(htmlMsg, true) // Use this or above line.

        helper.setTo(mail.to)
        helper.setSubject(mail.subject)
        helper.setFrom(mail.from)
        mailSender.send(mimeMessage)
    }

    private fun mailTemplatePathFor(mailType: MailType): String =
            when (mailType) {
                MailType.SIGN_UP -> mailType.path
                else -> throw UnsupportedOperationException("mail tpe not supported")
            }


}