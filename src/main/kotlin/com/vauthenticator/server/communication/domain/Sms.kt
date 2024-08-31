package com.vauthenticator.server.communication.domain

import com.vauthenticator.server.account.Account

data class SmsMessage(val phoneNumber: String, val message: String)


fun interface SmsSenderService {
    fun sendFor(account: Account, smsContext: MessageContext)
}

fun interface SmsMessageFactory {
    fun makeSmsMessageFor(account: Account, requestContext: MessageContext): SmsMessage
}

