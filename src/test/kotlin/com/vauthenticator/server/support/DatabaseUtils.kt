package com.vauthenticator.server.support

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI


object DatabaseUtils {
    const val dynamoClientApplicationTableName: String = "TESTING_VAuthenticator_ClientApplication"
    const val dynamoAccountTableName: String = "TESTING_VAuthenticator_Account"
    const val dynamoRoleTableName: String = "TESTING_VAuthenticator_Role"
    const val dynamoAccountRoleTableName: String = "TESTING_VAuthenticator_Account_Role"
    const val dynamoSignatureKeysTableName: String = "TESTING_VAuthenticator_Signature_Keys"
    const val dynamoMfaKeysTableName: String = "TESTING_VAuthenticator_Mfa_Keys"
    const val dynamoTicketTableName: String = "TESTING_VAuthenticator_ticket"
    const val dynamoMfaAccountMethodsTableName: String = "TESTING_VAuthenticator_Mfa_Account_Methods"

    val dynamoDbClient: DynamoDbClient = DynamoDbClient.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create("ACCESS_KEY_ID", "SECRET_ACCESS_KEY")
            )
        ).region(Region.US_EAST_1)
        .endpointOverride(URI.create("http://localhost:8000"))
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

    fun resetDatabase() {
        try {
            dynamoDbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoClientApplicationTableName)
                    .build()
            )
            dynamoDbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoAccountTableName)
                    .build()
            )
            dynamoDbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoRoleTableName)
                    .build()
            )
            dynamoDbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoAccountRoleTableName)
                    .build()
            )
            dynamoDbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoSignatureKeysTableName)
                    .build()
            )
            dynamoDbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoMfaKeysTableName)
                    .build()
            )
            dynamoDbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoTicketTableName)
                    .build()
            )
            dynamoDbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoMfaAccountMethodsTableName)
                    .build()
            )
        } catch (e: java.lang.Exception) {
        }
        try {
            createDynamoClientApplicationTable()
            createDynamoAccountTable()
            createDynamoRoleTable()
            createDynamoAccountRoleTable()
            createDynamoSignatureKeysTable()
            createDynamoMfaKeysTable()
            createDynamoTicketTable()
            createDynamoMfaAccountMethodsTable()
        } catch (e: java.lang.Exception) {
        }

    }

    private fun createDynamoMfaAccountMethodsTable() {
        dynamoDbClient.createTable(
            CreateTableRequest.builder()
                .tableName(dynamoMfaAccountMethodsTableName)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("user_name")
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName("mfa_method")
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("user_name")
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName("mfa_method")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build()
        );
    }

    private fun createDynamoAccountRoleTable() {
        dynamoDbClient.createTable(
            CreateTableRequest.builder()
                .tableName(dynamoAccountRoleTableName)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("user_name")
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName("role_name")
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("user_name")
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName("role_name")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build()
        );
    }

    private fun createDynamoRoleTable() {
        dynamoDbClient.createTable(
            CreateTableRequest.builder()
                .tableName(dynamoRoleTableName)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("role_name")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("role_name")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build()
        )
    }

    private fun createDynamoAccountTable() {
        dynamoDbClient.createTable(
            CreateTableRequest.builder()
                .tableName(dynamoAccountTableName)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("user_name")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("user_name")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build()
        )
    }

    private fun createDynamoClientApplicationTable() {
        dynamoDbClient.createTable(
            CreateTableRequest.builder()
                .tableName(dynamoClientApplicationTableName)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("client_id")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("client_id")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build()
        )
    }

    private fun createDynamoSignatureKeysTable() {
        dynamoDbClient.createTable(
            CreateTableRequest.builder()
                .tableName(dynamoSignatureKeysTableName)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("key_id")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("key_id")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build()
        )
    }

    private fun createDynamoMfaKeysTable() {
        dynamoDbClient.createTable(
            CreateTableRequest.builder()
                .tableName(dynamoMfaKeysTableName)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("key_id")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("key_id")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build()
        );
    }

    private fun createDynamoTicketTable() {
        dynamoDbClient.createTable(
            CreateTableRequest.builder()
                .tableName(dynamoTicketTableName)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("ticket")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("ticket")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build()
        );
    }

}