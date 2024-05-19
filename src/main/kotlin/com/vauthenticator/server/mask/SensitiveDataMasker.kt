package com.vauthenticator.server.mask

interface SensitiveDataMasker {

    fun mask(sensitiveData: String): String
}

class SensitiveEmailMasker : SensitiveDataMasker {
    override fun mask(sensitiveData: String): String {
        val dividedMail = sensitiveData.split("@")
        val mail = dividedMail[0]
        val mailDomain = dividedMail[1]

        val maskedMail = mail.replaceRange(1, mail.length, "x".repeat(mail.length - 1))

        return "%s@%s".format(maskedMail, mailDomain)
    }

}