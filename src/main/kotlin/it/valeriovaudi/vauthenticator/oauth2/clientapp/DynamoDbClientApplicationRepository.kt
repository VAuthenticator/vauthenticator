package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.util.*

class DynamoDbClientApplicationRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoClientApplicationTableName: String
) : ClientApplicationRepository {
    override fun findOne(clientAppId: ClientAppId): Optional<ClientApplication> {
        return Optional.empty<ClientApplication>()
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
                .item(
                    mutableMapOf(
                        "client_id" to clientApp.clientAppId.content.asDynamoAttribute(),
                        "client_secret" to clientApp.secret.content.asDynamoAttribute(),
                        "resource_ids" to "oauth2".asDynamoAttribute(),
                        "scope" to clientApp.scopes.asDynamoAttribute(),
                        "authorized_grant_types" to clientApp.authorizedGrantTypes.asDynamoAttribute(),
                        "web_server_redirect_uri" to clientApp.webServerRedirectUri.content.asDynamoAttribute(),
                        "authorities" to clientApp.authorities.asDynamoAttribute(),
                        "access_token_validity" to clientApp.accessTokenValidity.asDynamoAttribute(),
                        "refresh_token_validity" to clientApp.refreshTokenValidity.asDynamoAttribute(),
                        "auto_approve" to clientApp.autoApprove.content.asDynamoAttribute(),
                        "post_logout_redirect_uris" to clientApp.postLogoutRedirectUri.content.asDynamoAttribute(),
                        "logout_uris" to clientApp.logoutUri.content.asDynamoAttribute(),
                        "federation" to clientApp.federation.name.asDynamoAttribute()
                    )
                )
                .build()
        )
    }

    override fun delete(clientAppId: ClientAppId) {
        TODO("Not yet implemented")
    }

}