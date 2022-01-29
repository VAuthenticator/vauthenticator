package it.valeriovaudi.vauthenticator.account

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import javax.websocket.Session

@Controller
class SignUpController {

    @GetMapping("/sign-up")
    fun view(session : Session){

    }
}