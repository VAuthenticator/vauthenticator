package com.vauthenticator.server.login

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import java.util.Optional.*
import kotlin.jvm.optionals.getOrElse

class CompositeLoginWorkflowEngine(
    val url: String,
    private val handlers: List<LoginWorkflowHandler>
) :
    LoginWorkflowEngine, AuthenticationSuccessHandler {
    private val defaultSuccessHandler: AuthenticationSuccessHandler

    init {
        this.defaultSuccessHandler = SimpleUrlAuthenticationSuccessHandler(url)
        this.defaultSuccessHandler.setAlwaysUseDefaultTargetUrl(true)
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        println("authentication on engine: $authentication")
        SecurityContextHolder.getContext().authentication = authentication
        this.defaultSuccessHandler.onAuthenticationSuccess(request, response, authentication)
    }

    override fun workflowsNextHop(session: HttpSession): LoginWorkflowHandler {
        val index = ofNullable(session.getAttribute("CompositeLoginWorkflowEngine_index")).getOrElse { 0 } as Int
        val nextHandlerIndex = index + 1

        return if (nextHandlerIndex > handlers.size) {
            DefaultLoginWorkflowHandler
        } else {
            session.setAttribute("CompositeLoginWorkflowEngine_index", nextHandlerIndex)
            handlers[index]
        }

    }

}

interface LoginWorkflowEngine {
    fun workflowsNextHop(session: HttpSession): LoginWorkflowHandler
}

object DefaultLoginWorkflowHandler : LoginWorkflowHandler {
    override fun view() = ""

    override fun canHandle(request: HttpServletRequest, response: HttpServletResponse) = false

}

interface LoginWorkflowHandler {
    fun view(): String

    fun canHandle(request: HttpServletRequest, response: HttpServletResponse): Boolean

}

const val LOGIN_ENGINE_BROKER_PAGE = "/login-workflow"

@Controller
class LoginWorkflowEngineController(private val engine: LoginWorkflowEngine) {


    val defaultNextHope = SavedRequestAwareAuthenticationSuccessHandler()
    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()

    @GetMapping(LOGIN_ENGINE_BROKER_PAGE)
    fun view(
        modelAndView: ModelAndView,
        session: HttpSession,
        authentication: Authentication?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val workflowsNextHop = engine.workflowsNextHop(session)
        println("workflowsNextHop: ${workflowsNextHop}")

        if (workflowsNextHop.canHandle(request, response)) {
            println("go to redirect on ${workflowsNextHop.view()}")
            redirectStrategy.sendRedirect(request, response, workflowsNextHop.view())
        } else {
            defaultNextHope.onAuthenticationSuccess(request, response, authentication)
        }
    }
}