package com.vauthenticator.server.oauth2.clientapp

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ClientApplicationEndPoint(
    private val clientApplicationRepository: ClientApplicationRepository,
    private var storeClientApplication: StoreClientApplication,
    private val readClientApplication: ReadClientApplication
) {

    @PutMapping("/api/client-applications/{clientAppId}")
    fun storeClientApplication(
        @PathVariable("clientAppId") clientAppId: String,
        @RequestBody clientAppRepresentation: ClientAppRepresentation
    ): ResponseEntity<Unit> {
        val aClientApp = ClientAppRepresentation.fromRepresentationToDomain(clientAppId, clientAppRepresentation)
        val storeWithPassword = clientAppRepresentation.storePassword
        storeClientApplication.store(aClientApp, storeWithPassword)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/api/client-applications/{clientAppId}")
    fun resetPasswordForClientApplication(
        @PathVariable("clientAppId") clientAppId: String,
        @RequestBody body: Map<String, String>
    ): ResponseEntity<Unit> {
        storeClientApplication.resetPassword(ClientAppId(clientAppId), Secret(body["secret"]!!))
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/api/client-applications")
    fun viewAllClientApplications() =
        readClientApplication.findAll()
            .map { ClientAppInListRepresentation.fromDomainToRepresentation(it) }
            .let {
                ResponseEntity.ok(it)
            }

    @GetMapping("/api/client-applications/{clientAppId}")
    fun viewAClientApplication(@PathVariable("clientAppId") clientAppId: String) =
        readClientApplication.findOne(ClientAppId(clientAppId))
            .map { ClientAppRepresentation.fromDomainToRepresentation(it) }
            .let {
                ResponseEntity.ok(it)
            }

    @DeleteMapping("/api/client-applications/{clientAppId}")
    fun deleteAClientApplication(@PathVariable("clientAppId") clientAppId: String): ResponseEntity<Unit> {
        clientApplicationRepository.delete(ClientAppId(clientAppId))
        return ResponseEntity.noContent().build()
    }

    @ExceptionHandler(ClientApplicationNotFound::class)
    fun clientApplicationNotFoundHandler() = ResponseEntity.notFound().build<Unit>()

}

data class ClientAppRepresentation(
    var clientAppName: String,
    var secret: String,
    var withPkce: Boolean,
    var storePassword: Boolean,
    var scopes: List<String>,
    var authorizedGrantTypes: List<String>,
    var webServerRedirectUri: String,
    var authorities: List<String>,
    var accessTokenValidity: Long,
    var refreshTokenValidity: Long,
    var postLogoutRedirectUri: String,
    var logoutUri: String
) {
    companion object {
        fun fromDomainToRepresentation(clientApplication: ClientApplication, storePassword: Boolean = false) =
            ClientAppRepresentation(
                clientAppName = clientApplication.clientAppId.content,
                secret = clientApplication.secret.content,
                storePassword = storePassword,
                withPkce = clientApplication.withPkce.content,
                scopes = clientApplication.scopes.content.map { it.content },
                authorizedGrantTypes = clientApplication.authorizedGrantTypes.content.map { it.name.lowercase() },
                webServerRedirectUri = clientApplication.webServerRedirectUri.content,
                authorities = clientApplication.authorities.content.map { it.content },
                accessTokenValidity = clientApplication.accessTokenValidity.content,
                refreshTokenValidity = clientApplication.refreshTokenValidity.content,
                postLogoutRedirectUri = clientApplication.postLogoutRedirectUri.content,
                logoutUri = clientApplication.logoutUri.content,
            )

        fun fromRepresentationToDomain(clientAppId: String, representation: ClientAppRepresentation) =
            ClientApplication(
                clientAppId = ClientAppId(clientAppId),
                secret = Secret(representation.secret),
                withPkce = WithPkce(representation.withPkce),
                scopes = Scopes(representation.scopes.map { Scope(it) }.toSet()),
                authorizedGrantTypes = AuthorizedGrantTypes(representation.authorizedGrantTypes.map { it.uppercase() }
                    .map { AuthorizedGrantType.valueOf(it) }),
                webServerRedirectUri = CallbackUri(representation.webServerRedirectUri),
                authorities = Authorities(representation.authorities.map { it.uppercase() }.map(::Authority).toSet()),
                accessTokenValidity = TokenTimeToLive(representation.accessTokenValidity),
                refreshTokenValidity = TokenTimeToLive(representation.refreshTokenValidity),
                postLogoutRedirectUri = PostLogoutRedirectUri(representation.postLogoutRedirectUri),
                logoutUri = LogoutUri(representation.logoutUri)
            )
    }
}


data class ClientAppInListRepresentation(
    var clientAppId: String,
    var clientAppName: String,
    var scopes: List<String>,
    var authorizedGrantTypes: List<String>
) {
    companion object {
        fun fromDomainToRepresentation(clientApplication: ClientApplication) =
            ClientAppInListRepresentation(
                clientAppName = clientApplication.clientAppId.content,
                clientAppId = clientApplication.clientAppId.content,
                scopes = clientApplication.scopes.content.map { it.content },
                authorizedGrantTypes = clientApplication.authorizedGrantTypes.content.map { it.name.lowercase() },
            )
    }
}