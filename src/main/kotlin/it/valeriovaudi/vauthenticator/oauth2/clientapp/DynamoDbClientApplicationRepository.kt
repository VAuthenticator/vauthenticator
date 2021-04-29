package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.extentions.valueAsBoolFor
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoClientApplicationConverter.fromDomainToDynamo
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.util.*

class DynamoDbClientApplicationRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoClientApplicationTableName: String
) : ClientApplicationRepository {
    override fun findOne(clientAppId: ClientAppId): Optional<ClientApplication> {
        return Optional.ofNullable(
            dynamoDbClient.getItem(
                GetItemRequest.builder()
                    .tableName(dynamoClientApplicationTableName)
                    .key(
                        mutableMapOf(
                            "client_id" to clientAppId.content.asDynamoAttribute()
                        )
                    )
                    .build()
            ).item()
                .let {
                    ClientApplication(
                        clientAppId = ClientAppId(it.valueAsStringFor("client_id")),
                        secret = Secret(it.valueAsStringFor("client_secret")),
                        resourceIds = ResourceIds(listOf(ResourceId(it.valueAsStringFor("resource_ids")))),
                        scopes = Scopes(it["scopes"]?.ss()!!.map { Scope(it) }),
                        authorizedGrantTypes = AuthorizedGrantTypes(
                            it["authorized_grant_types"]?.ss()!!.map { AuthorizedGrantType.valueOf(it) }),
                        webServerRedirectUri = CallbackUri(it.valueAsStringFor("web_server_redirect_uri")),
                        authorities = Authorities(it["authorities"]?.ss()!!.map { Authority(it) }),
                        accessTokenValidity = TokenTimeToLive(it["access_token_validity"]?.n()!!.toInt()),
                        refreshTokenValidity = TokenTimeToLive(it["refresh_token_validity"]?.n()!!.toInt()),
                        autoApprove = AutoApprove(it.valueAsBoolFor("auto_approve")),
                        postLogoutRedirectUri = PostLogoutRedirectUri(it.valueAsStringFor("post_logout_redirect_uris")),
                        logoutUri = LogoutUri(it.valueAsStringFor("logout_uris")),
                        federation = Federation(it.valueAsStringFor("federation"))
                    )
                }
        )
    }

    override fun findByFederation(federation: Federation): Iterable<ClientApplication> {
        TODO("Not yet implemented")
    }

    override fun findAll(): Iterable<ClientApplication> {
        TODO("Not yet implemented")
    }

    override fun save(clientApp: ClientApplication) {
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(dynamoClientApplicationTableName)
                .item(fromDomainToDynamo(clientApp))
                .build()
        )
    }

    override fun delete(clientAppId: ClientAppId) {
        TODO("Not yet implemented")
    }

}