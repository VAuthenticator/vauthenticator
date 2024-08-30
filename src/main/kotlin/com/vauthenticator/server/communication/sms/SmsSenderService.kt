package com.vauthenticator.server.communication.sms

import com.vauthenticator.server.account.Account
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest

typealias SmsContext = Map<String, String>

fun interface SmsSenderService {
    fun sendFor(account: Account, smsContext: SmsContext)
}

fun interface SmsMessageFactory {
    fun makeSmsMessageFor(account: Account, requestContext: SmsContext): SmsMessage
}


class SnsSmsSenderService(
    private val snsClient: SnsClient,
    private val smsMessageFactory: SmsMessageFactory
) : SmsSenderService {
    override fun sendFor(account: Account, smsContext: SmsContext) {
        val smsMessage = smsMessageFactory.makeSmsMessageFor(account, smsContext)
        snsClient.publish(
            PublishRequest.builder()
                .message(smsMessage.message)
                .phoneNumber(smsMessage.phoneNumber.replace(" ", ""))
                .build()
        )
    }

}