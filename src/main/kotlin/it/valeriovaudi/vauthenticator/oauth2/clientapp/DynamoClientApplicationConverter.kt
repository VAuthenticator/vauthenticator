package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

const val resourceId: String = "oauth2-resource"

object DynamoClientApplicationConverter {

    fun fromDomainToDynamo(
        clientApp: ClientApplication
    ): MutableMap<String, AttributeValue> {
        return mutableMapOf(
            "client_id" to clientApp.clientAppId.content.asDynamoAttribute(),
            "client_secret" to clientApp.secret.content.asDynamoAttribute(),
            "resource_ids" to resourceId.asDynamoAttribute(),
            "scopes" to clientApp.scopes.asDynamoAttribute(),
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
    }

}