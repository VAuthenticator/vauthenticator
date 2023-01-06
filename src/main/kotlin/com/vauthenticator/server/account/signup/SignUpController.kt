package com.vauthenticator.server.account.signup

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.SessionAttributes

@Controller
@SessionAttributes("features")
class SignUpController {

    @GetMapping("/sign-up")
    fun view(@ModelAttribute("features") features: List<String>, model: Model): String {
        model.addAttribute("assetBundle", "signup_bundle.js")
        return "template"
    }
}