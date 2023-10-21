package com.vauthenticator.server.password.changepassword

import com.vauthenticator.server.account.AccountMandatoryAction
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.login.workflow.LoginWorkflowHandler
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

const val CHANGE_PASSWORD_URL = "/change-password"

class ChangePasswordLoginWorkflowHandler(
    val accountRepository: AccountRepository,
    val handler: AuthenticationSuccessHandler
) : LoginWorkflowHandler {

    override fun view(): String = CHANGE_PASSWORD_URL

    override fun canHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Boolean {
        val username = SecurityContextHolder.getContext().authentication.name
        return accountRepository.accountFor(username)
            .map { account -> account.mandatoryAction === AccountMandatoryAction.RESET_PASSWORD }
            .orElseGet { false }
    }

}
