package it.valeriovaudi.vauthenticator.oauth2.clientapp

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientApplicationEndPoint(private val clientApplicationRepository: ClientApplicationRepository) {

    @PutMapping("/api/client-applications/{clientAppId}")
    fun storeClientApplication(@PathVariable("clientAppId") clientAppId: String, clientAppRepresentation: ClientAppRepresentation) =
            ResponseEntity.noContent().build<Unit>()

    @GetMapping("/api/client-applications")
    fun viewAllClientApplications() =
            clientApplicationRepository.findAll()
                    .map { ClientAppInListRepresentation.fromDomainToRepresentation(it) }
                    .let {
                        ResponseEntity.ok(it)
                    }

    @GetMapping("/api/client-applications/{clientAppId}")
    fun viewAClientApplication(@PathVariable("clientAppId") clientAppId: String) =
            clientApplicationRepository.findOne(ClientAppId(clientAppId))
                    .map { ClientAppRepresentation.fromDomainToRepresentation(it) }
                    .let {
                        ResponseEntity.ok(it)
                    }

}

data class ClientAppRepresentation(var clientAppName: String,
                                   var secret: String,
                                   var setSecret: Boolean,
                                   var scopes: List<String>,
                                   var authorizedGrantTypes: List<String>,
                                   var webServerRedirectUri: String,
                                   var authorities: List<String>,
                                   var accessTokenValidity: Int,
                                   var refreshTokenValidity: Int,
                                   var postLogoutRedirectUri: String,
                                   var logoutUri: String,
                                   var federation: String) {
    companion object {
        fun fromDomainToRepresentation(clientApplication: ClientApplication) =
                ClientAppRepresentation(
                        clientAppName = clientApplication.clientAppId.content,
                        secret = clientApplication.secret.content(),
                        setSecret = false,
                        scopes = clientApplication.scopes.content.map { it.content },
                        authorizedGrantTypes = clientApplication.authorizedGrantTypes.content.map { it.name.toLowerCase() },
                        webServerRedirectUri = clientApplication.webServerRedirectUri.content,
                        authorities = clientApplication.authorities.content.map { it.content },
                        accessTokenValidity = clientApplication.accessTokenValidity.content,
                        refreshTokenValidity = clientApplication.refreshTokenValidity.content,
                        postLogoutRedirectUri = clientApplication.postLogoutRedirectUri.content,
                        logoutUri = clientApplication.logoutUri.content,
                        federation = clientApplication.federation.name
                )
    }
}


data class ClientAppInListRepresentation(var clientAppId: String,
                                         var clientAppName: String,
                                         var scopes: List<String>,
                                         var authorizedGrantTypes: List<String>,
                                         var federation: String) {
    companion object {
        fun fromDomainToRepresentation(clientApplication: ClientApplication) =
                ClientAppInListRepresentation(
                        clientAppName = clientApplication.clientAppId.content,
                        clientAppId = clientApplication.clientAppId.content,
                        scopes = clientApplication.scopes.content.map { it.content },
                        authorizedGrantTypes = clientApplication.authorities.content.map { it.content },
                        federation = clientApplication.federation.name
                )
    }
}