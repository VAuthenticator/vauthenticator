package com.vauthenticator.server.extentions

import jakarta.servlet.http.HttpServletRequest
import java.util.*


fun HttpServletRequest.oauth2ClientId(): Optional<String> {
    return Optional.ofNullable((this.getParameter("client_id") as String?))

}