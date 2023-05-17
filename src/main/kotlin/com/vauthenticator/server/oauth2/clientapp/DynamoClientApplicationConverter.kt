package com.vauthenticator.server.oauth2.clientapp

import com.vauthenticator.server.extentions.*
import software.amazon.awssdk.services.dynamodb.model.AttributeValue


object DynamoClientApplicationConverter {

    fun fromDomainToDynamo(
            clientApp: ClientApplication
    ): MutableMap<String, AttributeValue> {
        val dynamoDocument = mutableMapOf(
                "client_id" to clientApp.clientAppId.content.asDynamoAttribute(),
                "client_secret" to clientApp.secret.content.asDynamoAttribute(),
                "scopes" to clientApp.scopes.asDynamoAttribute(),
                "authorized_grant_types" to clientApp.authorizedGrantTypes.asDynamoAttribute(),
                "web_server_redirect_uri" to clientApp.webServerRedirectUri.content.asDynamoAttribute(),
                "access_token_validity" to clientApp.accessTokenValidity.asDynamoAttribute(),
                "refresh_token_validity" to clientApp.refreshTokenValidity.asDynamoAttribute(),
                "auto_approve" to clientApp.autoApprove.content.asDynamoAttribute(),
                "post_logout_redirect_uris" to clientApp.postLogoutRedirectUri.content.asDynamoAttribute(),
                "logout_uris" to clientApp.logoutUri.content.asDynamoAttribute()
        )
        if (clientApp.authorities.content.isNotEmpty()) {
            dynamoDocument["authorities"] = clientApp.authorities.asDynamoAttribute()
        }
        return dynamoDocument
    }

    fun fromDynamoToDomain(
            dynamoPayload: MutableMap<String, AttributeValue>
    ): ClientApplication {
        return ClientApplication(
                clientAppId = ClientAppId(dynamoPayload.valueAsStringFor("client_id")),
                secret = Secret(dynamoPayload.valueAsStringFor("client_secret")),
                scopes = Scopes(dynamoPayload.valuesAsListOfStringFor("scopes").map { Scope(it) }.toSet()),
                authorizedGrantTypes = AuthorizedGrantTypes(
                        dynamoPayload.valuesAsListOfStringFor("authorized_grant_types")
                                .map { it.uppercase() }
                                .map { AuthorizedGrantType.valueOf(it) }),
                webServerRedirectUri = CallbackUri(dynamoPayload.valueAsStringFor("web_server_redirect_uri")),
                authorities = Authorities(dynamoPayload.valuesAsListOfStringFor("authorities").map { Authority(it) }.toSet()),
                accessTokenValidity = TokenTimeToLive(dynamoPayload.valueAsLongFor("access_token_validity")),
                refreshTokenValidity = TokenTimeToLive(dynamoPayload.valueAsLongFor("refresh_token_validity")),
                autoApprove = AutoApprove(dynamoPayload.valueAsBoolFor("auto_approve")),
                postLogoutRedirectUri = PostLogoutRedirectUri(dynamoPayload.valueAsStringFor("post_logout_redirect_uris")),
                logoutUri = LogoutUri(dynamoPayload.valueAsStringFor("logout_uris"))
        )
    }

}