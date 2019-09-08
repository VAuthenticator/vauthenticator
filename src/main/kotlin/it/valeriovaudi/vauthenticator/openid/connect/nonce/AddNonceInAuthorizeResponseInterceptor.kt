package it.valeriovaudi.vauthenticator.openid.connect.nonce

import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AddNonceInAuthorizeResponseInterceptor(private val nonceStore: NonceStore) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (shouldBeFiltered(request)) {
            println("state ${request.getParameter("state")}")
            println("nonce ${request.getParameter("nonce")}")
            nonceStore.store(request.getParameter("state"), request.getParameter("nonce"))
        }

        chain.doFilter(request, response)
    }


    //todo add servlet context path not written in the cde
    private fun shouldBeFiltered(request: HttpServletRequest) =
            request.requestURI.equals("/vauthenticator/oauth/authorize") &&
                    request.getParameter("nonce") != null &&
                    request.getParameter("state") != null


}
