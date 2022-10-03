package it.valeriovaudi.vauthenticator.account.mailverification

import org.springframework.web.bind.annotation.PathVariable

class MailVerificationEndPoint {

    fun sendVerifyMail(@PathVariable mail: String): Unit = TODO()

    fun verifyMail(@PathVariable mail: String): Unit = TODO()

}