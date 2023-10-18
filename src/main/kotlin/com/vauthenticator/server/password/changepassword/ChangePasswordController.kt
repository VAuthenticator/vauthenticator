package com.vauthenticator.server.password.changepassword

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class ChangePasswordController(
    private val publisher: ApplicationEventPublisher,
    private val successHandler: AuthenticationSuccessHandler
) {


    @GetMapping("/change-password")
    fun view(model: Model): String {
        model.addAttribute("assetBundle", "changePassword_bundle.js")
        return "template"
    }

    @PostMapping("/change-password")
    fun resetPassword(
        authentication: Authentication,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) : String {
        return "redirect:/login-workflow"
    }
}
