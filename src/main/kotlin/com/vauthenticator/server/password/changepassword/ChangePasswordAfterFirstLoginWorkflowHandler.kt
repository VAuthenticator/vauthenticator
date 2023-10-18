package com.vauthenticator.server.password.changepassword

import com.vauthenticator.server.login.LoginWorkflowHandler
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler

class ChangePasswordAfterFirstLoginWorkflowHandler(val url: String) :
    LoginWorkflowHandler {

    private val handler = SimpleUrlAuthenticationSuccessHandler(url)

    init {
        handler.setAlwaysUseDefaultTargetUrl(true)
    }

    override fun view(): String = "/change-password"

    override fun canHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Boolean {
        return true
    }

}
