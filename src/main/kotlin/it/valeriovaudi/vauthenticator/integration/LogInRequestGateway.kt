package it.valeriovaudi.vauthenticator.integration

import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.security.core.userdetails.UserDetails


@MessagingGateway
interface LogInRequestGateway {

    @Gateway(requestChannel = "authServerAccountServiceBridgeInboundChannel",
            replyChannel = "authServerAccountServiceBridgeOutboundChannel",
            replyTimeout = (60 * 1000).toLong())
    fun getPrincipleByUserName(userName: String): UserDetails
}
