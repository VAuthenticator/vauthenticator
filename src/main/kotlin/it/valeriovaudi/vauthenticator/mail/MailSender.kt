package it.valeriovaudi.vauthenticator.mail

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

data class MailMessage(val to: String, val from: String, val subject: String, val text: String)

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