package it.valeriovaudi.vauthenticator.support

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest

object TestingFixture {

    val dynamoRoleTableName: String = System.getenv("STAGING_DYNAMO_DB_ROLE_TABLE_NAME")
    val dynamoAccountTableName: String = System.getenv("STAGING_DYNAMO_DB_ACCOUNT_TABLE_NAME")
    val dynamoAccountRoleTableName: String = System.getenv("STAGING_DYNAMO_DB_ACCOUNT_ROLE_TABLE_NAME")
    val dynamoClientApplicationTableName: String = System.getenv("STAGING_DYNAMO_DB_CLIENT_APPLICATION_TABLE_NAME")

    val dynamoDbClient: DynamoDbClient = DynamoDbClient.builder()
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build()

    fun initRoleTests(roleRepository: DynamoDbClient) {
        val roleName = AttributeValue.builder().s("a_role").build()
        val description = AttributeValue.builder().s("A_ROLE").build()
        val item = PutItemRequest.builder()
            .tableName(dynamoRoleTableName)
            .item(
                mutableMapOf(
                    "role_name" to roleName,
                    "description" to description
                )
            )
            .build()
        roleRepository.putItem(item)
    }

    fun resetDatabase(client: DynamoDbClient) {
        scanFor(client, dynamoRoleTableName, "role_name")
            .forEach {
                val deleteItemRequest = DeleteItemRequest.builder().tableName(dynamoRoleTableName)
                    .key(
                        mutableMapOf(
                            "role_name" to it.valueAsStringFor("role_name").asDynamoAttribute()
                        )
                    )
                    .build()
                client.deleteItem(deleteItemRequest)
            }

        scanFor(client, dynamoAccountTableName, "user_name")
            .forEach {
                val deleteItemRequest = DeleteItemRequest.builder().tableName(dynamoAccountTableName)
                    .key(
                        mutableMapOf(
                            "user_name" to it.valueAsStringFor("user_name").asDynamoAttribute(),
                        )
                    )
                    .build()
                client.deleteItem(deleteItemRequest)
            }

        scanFor(client, dynamoAccountRoleTableName, "user_name", "role_name")
            .forEach {
                val deleteItemRequest = DeleteItemRequest.builder().tableName(dynamoAccountRoleTableName)
                    .key(
                        mutableMapOf(
                            "user_name" to it.valueAsStringFor("user_name").asDynamoAttribute(),
                            "role_name" to it.valueAsStringFor("role_name").asDynamoAttribute()
                        )
                    )
                    .build()
                client.deleteItem(deleteItemRequest)
            }

        scanFor(client, dynamoClientApplicationTableName, "client_id")
            .forEach {
                val deleteItemRequest = DeleteItemRequest.builder().tableName(dynamoClientApplicationTableName)
                    .key(
                        mutableMapOf(
                            "client_id" to it.valueAsStringFor("client_id").asDynamoAttribute(),
                        )
                    )
                    .build()
                client.deleteItem(deleteItemRequest)
            }
    }

    private fun scanFor(
        client: DynamoDbClient,
        tableName: String,
        vararg attributeToGet: String
    ): MutableList<MutableMap<String, AttributeValue>> =
        try {
            client.scan(
                ScanRequest.builder()
                    .tableName(tableName)
                    .attributesToGet(*attributeToGet)
                    .build()
            ).items()
        } catch (e: Exception) {
            println("e.message ${e.message}")
            mutableListOf()
        }

    fun idTokenFor(federation: String): String {
        val macSigner = MACSigner("123123123123123123123123123123123123123123123123123123123123")
        val plainHeader = JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build()
        val jwtClaimsSet = JWTClaimsSet.Builder()
            .claim("federation", federation)
            .build()

        val plainJWT = SignedJWT(plainHeader, jwtClaimsSet)
        plainJWT.sign(macSigner)
        return plainJWT.serialize()
    }
}