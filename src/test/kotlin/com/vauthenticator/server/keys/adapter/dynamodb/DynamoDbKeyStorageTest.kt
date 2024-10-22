package com.vauthenticator.server.keys.adapter.dynamodb

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.keys.adapter.AbstractKeyStorageTest
import com.vauthenticator.server.keys.adapter.dynamo.DynamoDbKeyStorage
import com.vauthenticator.server.keys.domain.KeyPurpose
import com.vauthenticator.server.keys.domain.KeyStorage
import com.vauthenticator.server.keys.domain.Kid
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDbClient
import com.vauthenticator.server.support.DynamoDbUtils.dynamoMfaKeysTableName
import com.vauthenticator.server.support.DynamoDbUtils.dynamoSignatureKeysTableName
import com.vauthenticator.server.support.DynamoDbUtils.resetDynamoDb
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class DynamoDbKeyStorageTest : AbstractKeyStorageTest() {

    override fun initKeyStorage(): KeyStorage = DynamoDbKeyStorage(
        clock(),
        dynamoDbClient,
        dynamoSignatureKeysTableName,
        dynamoMfaKeysTableName
    )

    override fun resetDatabase() {
        resetDynamoDb()
    }

    override fun getActual(kid: Kid, keyPurpose: KeyPurpose): MutableMap<String, AttributeValue> =
        dynamoDbClient.getItem(
            GetItemRequest.builder().tableName(tableNameFor(keyPurpose))
                .key(
                    mapOf(
                        "key_id" to kid.content().asDynamoAttribute()
                    )
                )
                .build()
        ).item()


    private fun tableNameFor(keyPurpose: KeyPurpose) =  when (keyPurpose) {
        KeyPurpose.MFA -> dynamoMfaKeysTableName
        KeyPurpose.SIGNATURE -> dynamoSignatureKeysTableName
    }
}