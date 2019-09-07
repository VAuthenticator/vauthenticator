package it.valeriovaudi.vauthenticator.openidconnect.idtoken

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import java.io.Serializable

class TestableDefaultOAuth2AccessToken(var additionalInfo: Map<String, Any> = mutableMapOf(),
                                       var clientAppScope: Set<String> = emptySet()) : DefaultOAuth2AccessToken("") {

    override fun getAdditionalInformation(): Map<String, Any> {
        return additionalInfo
    }

    override fun setAdditionalInformation(additionalInformation: Map<String, Any>) {
        additionalInfo = additionalInformation
    }

    override fun getScope() = clientAppScope
}

class TestableOAuth2Authentication : OAuth2Authentication(null, TestableAuthentication("USER_NAME")) {
    override fun getPrincipal() = userAuthentication

    override fun getOAuth2Request() =
            object : OAuth2Request() {
                override fun getClientId() = "A_CLIENT_APPLICATION_ID"
                override fun getExtensions() = mutableMapOf("nonce" to "A_NONCE")
            }

}

class TestableAuthentication(val userName: String) : Authentication {
    override fun getAuthorities() = emptyList<GrantedAuthority>()

    override fun setAuthenticated(isAuthenticated: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getName() = userName


    override fun getCredentials(): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPrincipal(): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isAuthenticated(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDetails(): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}