package com.vauthenticator.server.web

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class ErrorPagesTemplateController : ErrorController {

    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest,
                    exception: Exception,
                    model: Model): String {
        when (request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) as Int) {
            400 -> {
                model.addAttribute("errorPageTitle", "Page not found")
                model.addAttribute("errorMessage", "Page not found")
            }

            404 -> {
                println(exception.message)
                model.addAttribute("errorPageTitle", "Page not found 111111111111111")
                model.addAttribute("errorMessage", "Page not found")
            }

            500 -> {
                model.addAttribute("errorPageTitle", "Page not found")
                model.addAttribute("errorMessage", "Page not found")
            }
        }

        // display generic error
        return "error-template";
    }
}