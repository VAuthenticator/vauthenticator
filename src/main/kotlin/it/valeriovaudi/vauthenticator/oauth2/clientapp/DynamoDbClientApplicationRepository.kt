package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.extentions.filterEmptyAccountMetadata
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoClientApplicationConverter.fromDomainToDynamo
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoClientApplicationConverter.fromDynamoToDomain
import org.springframework.security.crypto.password.PasswordEncoder
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.util.*

class DynamoDbClientApplicationRepository(
        private val dynamoDbClient: DynamoDbClient,
        private val dynamoClientApplicationTableName: String
) : ClientApplicationRepository {
    override fun findOne(clientAppId: ClientAppId): Optional<ClientApplication> {
        return Optional.of(clientAppId.content)
            .flatMap { if (it.isEmpty()) Optional.empty() else Optional.of(clientAppId) }
            .map {
                dynamoDbClient.getItem(
                    GetItemRequest.builder()
                        .tableName(dynamoClientApplicationTableName)
                        .key(
                            mutableMapOf(
                                "client_id" to it.content.asDynamoAttribute()
                            )
                        )
                        .build()
                )
                    .item()
            }
            .flatMap { it.filterEmptyAccountMetadata() }
            .map { fromDynamoToDomain(it) }
    }

    override fun findLogoutUriByFederation(federation: Federation): Iterable<LogoutUri> {
        return dynamoDbClient.query(
            QueryRequest.builder()
                .tableName(dynamoClientApplicationTableName)
                .scanIndexForward(false)
                .indexName("FederationIndex")
                .scanIndexForward(false)
                .projectionExpression("logout_uris")
                .keyConditionExpression("federation = :federation")
                .expressionAttributeValues(
                    mapOf(
                        ":federation" to federation.name.asDynamoAttribute()
                    )
                )
                .build()
        ).items()
            .map { LogoutUri(it.valueAsStringFor("logout_uris")) }
    }

    override fun findAll(): Iterable<ClientApplication> {
        return dynamoDbClient.scan(
            ScanRequest.builder()
                .tableName(dynamoClientApplicationTableName)
                .build()
        ).items()
            .map {
                fromDynamoToDomain(it)
            }
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
        dynamoDbClient.deleteItem(
            DeleteItemRequest.builder()
                .tableName(dynamoClientApplicationTableName)
                .key(
                    mutableMapOf(
                        "client_id" to clientAppId.content.asDynamoAttribute()
                    )
                )
                .build()
        )
    }

}