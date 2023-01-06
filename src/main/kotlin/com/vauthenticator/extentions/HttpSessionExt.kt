package com.vauthenticator.extentions

import jakarta.servlet.http.HttpSession
import org.springframework.security.web.savedrequest.DefaultSavedRequest
import java.util.*

fun HttpSession.oauth2ClientId(): Optional<String> {
    val attribute = Optional.ofNullable(this.getAttribute("SPRING_SECURITY_SAVED_REQUEST"))
    return attribute.flatMap { savedRequest ->
        when (savedRequest) {
            is DefaultSavedRequest -> clientIdFromSessionWithinA(savedRequest)
            else -> Optional.empty()
        }

    }

}

private fun clientIdFromSessionWithinA(defaultSavedRequest: DefaultSavedRequest): Optional<String> {
    return if (defaultSavedRequest.parameterNames.contains("client_id")) {
        Optional.ofNullable(defaultSavedRequest.getParameterValues("client_id").firstOrNull())
    } else {
        Optional.empty()
    }

}