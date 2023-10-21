package com.vauthenticator.server.login.workflow

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

const val LOGIN_ENGINE_BROKER_PAGE = "/login-workflow"

@Controller
class LoginWorkflowEngineController(
    private val engine: LoginWorkflowEngine
) {

    private val logger = LoggerFactory.getLogger(LoginWorkflowEngineController::class.java)

    val defaultNextHope = SavedRequestAwareAuthenticationSuccessHandler()
    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()

    @GetMapping(LOGIN_ENGINE_BROKER_PAGE)
    fun view(
        session: HttpSession,
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val workflowsNextHop = engine.workflowsNextHop(session)
        logger.debug("workflowsNextHop: $workflowsNextHop")

        if (workflowsNextHop.canHandle(request, response)) {
            logger.debug("go to redirect on ${workflowsNextHop.view()}")
            redirectStrategy.sendRedirect(request, response, workflowsNextHop.view())
        } else {
            defaultNextHope.onAuthenticationSuccess(request, response, authentication)
        }
    }
}