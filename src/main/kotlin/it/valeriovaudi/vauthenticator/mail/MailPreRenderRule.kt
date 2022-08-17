package it.valeriovaudi.vauthenticator.mail

import it.valeriovaudi.vauthenticator.account.Account

interface MailPreRenderRule {

    fun apply(account: Account, requestContext: MailContext): MailContext

}

class SimpleMailPreRenderRule : MailPreRenderRule {

    override fun apply(account: Account, requestContext: MailContext): MailContext {
        return mapOf(
                "enabled" to account.enabled,
                "username" to account.username,
                "authorities" to account.authorities,
                "email" to account.email,
                "firstName" to account.firstName,
                "lastName" to account.lastName,
                "birthDate" to account.birthDate.iso8601FormattedDate(),
                "phone" to account.phone.formattedPhone(),
        ) + requestContext
    }

}