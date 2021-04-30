package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.extentions.*
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoClientApplicationConverter.fromDomainToDynamo
import it.valeriovaudi.vauthenticator.oauth2.clientapp.DynamoClientApplicationConverter.fromDynamoToDomain
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.util.*

class DynamoDbClientApplicationRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoClientApplicationTableName: String
) : ClientApplicationRepository {
    override fun findOne(clientAppId: ClientAppId): Optional<ClientApplication> {
        return Optional.of(clientAppId.content)
            .flatMap { if(it.isEmpty()) Optional.empty() else Optional.of(clientAppId) }
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