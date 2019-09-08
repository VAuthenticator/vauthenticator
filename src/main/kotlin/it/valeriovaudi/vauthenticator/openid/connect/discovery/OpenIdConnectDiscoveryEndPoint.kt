package it.valeriovaudi.vauthenticator.openid.connect.discovery

import it.valeriovaudi.vauthenticator.openid.connect.discovery.OpenIdConnectDiscovery.Companion.newOpenIdConnectDiscovery
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OpenIdConnectDiscoveryEndPoint(@Value("\${auth.oidcIss:}") private val issuer: String) {

    @GetMapping("/.well-known/openid-configuration")
    fun discovery() = ok(newOpenIdConnectDiscovery(issuer))

}