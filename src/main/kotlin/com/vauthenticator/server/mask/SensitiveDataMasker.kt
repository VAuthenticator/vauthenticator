package com.vauthenticator.server.mask

interface SensitiveDataMasker {

    fun mask(sensitiveData: String): String
}

class SensitiveEmailMasker : SensitiveDataMasker {
    override fun mask(sensitiveData: String): String {
        val dividedMail = sensitiveData.split("@")
        val email = dividedMail[0]
        val mailDomain = dividedMail[1]

        val maskedMail = email.replaceRange(1, email.length, "x".repeat(email.length - 1))

        return "%s@%s".format(maskedMail, mailDomain)
    }

}