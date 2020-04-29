package it.valeriovaudi.vauthenticator.oauth2.clientapp

/*client_id
resource_ids
client_secret
scope
authorized_grant_types
web_server_redirect_uri
authorities
access_token_validity
refresh_token_validity
additional_information
autoapprove
post_logout_redirect_uris
logout_uris*/
data class ClientApplication(val clientAppId: ClientAppId,
                             val secret: Secret,
                             val scope: List<Scope>,
                             val authorizedGrantTypes: List<AuthorizedGrantTypes>,
                             val webServerRedirectUri: Uri,
                             val authorities: List<Authority>,
                             val accessTokenValidity: TokenTimeToLive,
                             val refreshTokenValidity: TokenTimeToLive,
                             val autoApprove: Boolean,
                             val postLogoutRedirectUri: Uri,
                             val logoutUri: Uri,
                             val federation: Federation,
                             val resourceIds: List<ResourceId>
)

enum class AuthorizedGrantTypes { CLIENT_CREDENTIALS, PASSWORD, AUTHORIZATION_CODE, IMPLICIT }

data class ResourceId(val content: String)
data class ClientAppId(val content: String)
data class Secret(val content: String)
data class Uri(val content: String)
data class Scope(val content: String)
data class Authority(val content: String)
data class TokenTimeToLive(val content: Int)
data class Federation(val name: String)

interface ClientApplicationRepository {

    fun findOne(clientAppId: ClientAppId)

    fun findByFederation(federation: Federation): Page<ClientApplication>

    fun findAll(): Page<ClientApplication>

    fun save(clientApp: ClientApplication)

    fun delete(clientAppId: ClientAppId)
}

data class Page<T>(val content: Iterable<T>, val page: Int, val size: Int, val total: Long)

class JdbcClientApplicationRepository : ClientApplicationRepository {
    override fun findOne(clientAppId: ClientAppId) {
        TODO("Not yet implemented")
    }

    override fun findByFederation(federation: Federation): Page<ClientApplication> {
        TODO("Not yet implemented")
    }

    override fun findAll(): Page<ClientApplication> {
        TODO("Not yet implemented")
    }

    override fun save(clientApp: ClientApplication) {
        TODO("Not yet implemented")
    }

    override fun delete(clientAppId: ClientAppId) {
        TODO("Not yet implemented")
    }

}