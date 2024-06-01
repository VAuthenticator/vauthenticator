package com.vauthenticator.server.support

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI


object DynamoDbUtils {
    const val dynamoPasswordHistoryTableName: String = "TESTING_VAuthenticator_PasswordHistory"
    const val dynamoClientApplicationTableName: String = "TESTING_VAuthenticator_ClientApplication"
    const val dynamoAccountTableName: String = "TESTING_VAuthenticator_Account"
    const val dynamoRoleTableName: String = "TESTING_VAuthenticator_Role"
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
        .endpointOverride(URI.create("http://localhost:4566"))
        .build()

    fun dynamoDbClient(port: Int): DynamoDbClient = DynamoDbClient.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create("ACCESS_KEY_ID", "SECRET_ACCESS_KEY")
            )
        ).region(Region.US_EAST_1)
        .endpointOverride(URI.create("http://localhost:$port"))
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

    fun resetDynamoDb(dbClient: DynamoDbClient = dynamoDbClient) {
        try {
            dbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoPasswordHistoryTableName)
                    .build()
            )
            dbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoClientApplicationTableName)
                    .build()
            )
            dbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoAccountTableName)
                    .build()
            )
            dbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoRoleTableName)
                    .build()
            )
            dbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoSignatureKeysTableName)
                    .build()
            )
            dbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoMfaKeysTableName)
                    .build()
            )
            dbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoTicketTableName)
                    .build()
            )
            dbClient.deleteTable(
                DeleteTableRequest.builder()
                    .tableName(dynamoMfaAccountMethodsTableName)
                    .build()
            )
        } catch (e: java.lang.Exception) {
            println(e)
        }
        try {
            createDynamoPasswordHistoryTable(dbClient)
            createDynamoClientApplicationTable(dbClient)
            createDynamoAccountTable(dbClient)
            createDynamoRoleTable(dbClient)
            createDynamoSignatureKeysTable(dbClient)
            createDynamoMfaKeysTable(dbClient)
            createDynamoTicketTable(dbClient)
            createDynamoMfaAccountMethodsTable(dbClient)
        } catch (e: java.lang.Exception) {
            println(e)

        }

    }

    private fun createDynamoPasswordHistoryTable(dbClient: DynamoDbClient = dynamoDbClient) {
        dbClient.createTable(
            CreateTableRequest.builder()
                .tableName(dynamoPasswordHistoryTableName)
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("user_name")
                        .keyType(KeyType.HASH)
                        .build(),
                    KeySchemaElement.builder()
                        .attributeName("created_at")
                        .keyType(KeyType.RANGE)
                        .build()
                )
                .attributeDefinitions(
                    AttributeDefinition.builder()
                        .attributeName("user_name")
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                    AttributeDefinition.builder()
                        .attributeName("created_at")
                        .attributeType(ScalarAttributeType.N)

                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build()
        );
    }

    private fun createDynamoMfaAccountMethodsTable(dbClient: DynamoDbClient = dynamoDbClient) {
        dbClient.createTable(
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
        )
    }

    private fun createDynamoRoleTable(dbClient: DynamoDbClient = dynamoDbClient) {
        dbClient.createTable(
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

    private fun createDynamoAccountTable(dbClient: DynamoDbClient = dynamoDbClient) {
        dbClient.createTable(
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

    private fun createDynamoClientApplicationTable(dbClient: DynamoDbClient = dynamoDbClient) {
        dbClient.createTable(
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

    private fun createDynamoSignatureKeysTable(dbClient: DynamoDbClient = dynamoDbClient) {
        dbClient.createTable(
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

    private fun createDynamoMfaKeysTable(dbClient: DynamoDbClient = dynamoDbClient) {
        dbClient.createTable(
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

    private fun createDynamoTicketTable(dbClient: DynamoDbClient = dynamoDbClient) {
        dbClient.createTable(
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