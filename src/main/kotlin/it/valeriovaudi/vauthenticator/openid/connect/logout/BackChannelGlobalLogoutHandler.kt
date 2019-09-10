package it.valeriovaudi.vauthenticator.openid.connect.logout

import org.springframework.http.RequestEntity
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class BackChannelGlobalLogoutHandler(private val restTemplate: RestTemplate,
                                     private val registeredApplication: List<String>) : LogoutHandler {

    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
        println("logout")
        println(request.getHeader("Authorization"))
        registeredApplication.forEach {
            println("logout from : $it")
            println(restTemplate.postForEntity(it, RequestEntity.EMPTY, String::class.java))
        }
    }
}