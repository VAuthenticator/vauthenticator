package it.valeriovaudi.vauthenticator.mail

import com.hubspot.jinjava.Jinjava
import it.valeriovaudi.vauthenticator.document.DocumentRepository
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import javax.mail.internet.MimeMessage


data class MailMessage(val to: String, val from: String, val subject: String, val type: MailType = MailType.SIGN_UP, val context: Map<String, Any>)

enum class MailType(val path: String) {
    SIGN_UP("templates/sign-up.html"), EMAIL_VERIFICATION(""), RESET_PASSWORD("");
}

interface MailSenderService {

    fun send(mail: MailMessage)
}

interface MailTemplateResolver {

    fun compile(template: String, context: Map<String, Any>): String

}

class JinjavaMailTemplateResolver(private val engine: Jinjava) : MailTemplateResolver {
    override fun compile(template: String, context: Map<String, Any>): String {
        return engine.render(template, context)
    }

}

private const val MAIL_DOCUMENT_TYPE = "mail"

class JavaMailSenderService(private val documentRepository: DocumentRepository,
                            private val mailSender: JavaMailSender,
                            private val templateResolver: MailTemplateResolver) : MailSenderService {

    override fun send(mail: MailMessage) {
        val documentContent = documentRepository.loadDocument(MAIL_DOCUMENT_TYPE, mailTemplatePathFor(mail.type))
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, "utf-8")
        val htmlMsg = String(documentContent)
        helper.setText(templateResolver.compile(htmlMsg, mail.context), true) // Use this or above line.

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