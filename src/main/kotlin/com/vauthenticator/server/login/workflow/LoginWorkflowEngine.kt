package com.vauthenticator.server.login.workflow

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import java.util.*
import kotlin.jvm.optionals.getOrElse

interface LoginWorkflowEngine {
    fun workflowsNextHop(session: HttpSession): LoginWorkflowHandler
}

class CompositeLoginWorkflowEngine(
    private val handlers: List<LoginWorkflowHandler>,
    private val defaultSuccessHandler: AuthenticationSuccessHandler
) : LoginWorkflowEngine, AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        SecurityContextHolder.getContext().authentication = authentication
        this.defaultSuccessHandler.onAuthenticationSuccess(request, response, authentication)
    }

    override fun workflowsNextHop(session: HttpSession): LoginWorkflowHandler {
        val index = Optional.ofNullable(session.getAttribute("CompositeLoginWorkflowEngine_index")).getOrElse { 0 } as Int
        val nextHandlerIndex = index + 1

        return if (nextHandlerIndex > handlers.size) {
            DefaultLoginWorkflowHandler
        } else {
            session.setAttribute("CompositeLoginWorkflowEngine_index", nextHandlerIndex)
            handlers[index]
        }

    }

}

