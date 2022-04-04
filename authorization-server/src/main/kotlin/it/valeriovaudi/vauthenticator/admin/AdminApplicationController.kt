package it.valeriovaudi.vauthenticator.admin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AdminApplicationController {

    @GetMapping("/secure/admin/index")
    fun view() = "secure/admin"

}