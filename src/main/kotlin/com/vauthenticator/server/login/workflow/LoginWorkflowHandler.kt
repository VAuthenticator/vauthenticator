package com.vauthenticator.server.login.workflow

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse


const val LOGIN_ENGINE_BROKER_PAGE = "/login-workflow"

interface LoginWorkflowHandler {

    fun view(): String

    fun canHandle(request: HttpServletRequest, response: HttpServletResponse): Boolean
}

object DefaultLoginWorkflowHandler : LoginWorkflowHandler {
    override fun view() = ""

    override fun canHandle(request: HttpServletRequest, response: HttpServletResponse) = false

}