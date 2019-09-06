package it.valeriovaudi.vauthenticator.openidconnect.nonce

import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AddNonceInAuthorizeResponseInterceptor(private val nonceStore: NonceStore) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (shouldBeFiltered(request)) {
            nonceStore.store(request.getParameter("state"), request.getParameter("nonce"))
        }

        chain.doFilter(request, response)
    }


    private fun shouldBeFiltered(request: HttpServletRequest) =
            request.requestURI.equals("${request.servletPath}/oauth/authorize") &&
                    request.getParameter("nonce") != null &&
                    request.getParameter("state") != null


}
