package com.vauthenticator.server.oauth2.clientapp.adapter.dynamodb

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.filterEmptyMetadata
import com.vauthenticator.server.oauth2.clientapp.adapter.dynamodb.DynamoClientApplicationConverter.fromDomainToDynamo
import com.vauthenticator.server.oauth2.clientapp.adapter.dynamodb.DynamoClientApplicationConverter.fromDynamoToDomain
import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplication
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest
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
            .flatMap { it.filterEmptyMetadata() }
            .map { fromDynamoToDomain(it) }
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