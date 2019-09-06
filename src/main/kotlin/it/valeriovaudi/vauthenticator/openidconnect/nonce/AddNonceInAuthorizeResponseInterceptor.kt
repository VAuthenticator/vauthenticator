package it.valeriovaudi.vauthenticator.openidconnect.nonce

import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AddNonceInAuthorizeResponseInterceptor(private val nonceStore: NonceStore) : HandlerInterceptorAdapter() {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any) =
            request.getParameter("nonce") != null && request.getParameter("code") != null


    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        print("request.getParameter(\"nonce\") ${request.getParameter("nonce")}")
        nonceStore.store(request.getParameter("code"), request.getParameter("nonce"))
    }
}
