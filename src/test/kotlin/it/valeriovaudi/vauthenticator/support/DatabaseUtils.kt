package it.valeriovaudi.vauthenticator.support

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
    const val dynamoKeysTableName: String = "TESTING_VAuthenticator_Keys"
    const val dynamoMailVerificationTicketTableName: String = "TESTING_VAuthenticator_mail_verification_ticket"

    val dynamoDbClient: DynamoDbClient = DynamoDbClient.builder()
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("ACCESS_KEY_ID", "SECRET_ACCESS_KEY"))
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
            dynamoDbClient.deleteTable(DeleteTableRequest.builder()
                    .tableName(dynamoClientApplicationTableName)
                    .build())
            dynamoDbClient.deleteTable(DeleteTableRequest.builder()
                    .tableName(dynamoAccountTableName)
                    .build())
            dynamoDbClient.deleteTable(DeleteTableRequest.builder()
                    .tableName(dynamoRoleTableName)
                    .build())
            dynamoDbClient.deleteTable(DeleteTableRequest.builder()
                    .tableName(dynamoAccountRoleTableName)
                    .build())
            dynamoDbClient.deleteTable(DeleteTableRequest.builder()
                    .tableName(dynamoKeysTableName)
                    .build())
            dynamoDbClient.deleteTable(DeleteTableRequest.builder()
                    .tableName(dynamoMailVerificationTicketTableName)
                    .build())
        } catch (e: java.lang.Exception) {
        }
        try {
            createDynamoClientApplicationTable()
            createDynamoAccountTable()
            createDynamoRoleTable()
            createDynamoAccountRoleTable()
            createDynamoKeysTable()
            createDynamoMailVerificationTicketTable()
        } catch (e: java.lang.Exception) {
        }

    }

    private fun createDynamoAccountRoleTable() {
        dynamoDbClient.createTable(CreateTableRequest.builder()
                .tableName(dynamoAccountRoleTableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("user_name")
                        .keyType(KeyType.HASH)
                        .build(),
                        KeySchemaElement.builder()
                                .attributeName("role_name")
                                .keyType(KeyType.RANGE)
                                .build())
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("user_name")
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                        AttributeDefinition.builder()
                                .attributeName("role_name")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build());
    }

    private fun createDynamoRoleTable() {
        dynamoDbClient.createTable(CreateTableRequest.builder()
                .tableName(dynamoRoleTableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("role_name")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("role_name")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build())
    }

    private fun createDynamoAccountTable() {
        dynamoDbClient.createTable(CreateTableRequest.builder()
                .tableName(dynamoAccountTableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("user_name")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("user_name")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build())
    }

    private fun createDynamoClientApplicationTable() {
        dynamoDbClient.createTable(CreateTableRequest.builder()
                .tableName(dynamoClientApplicationTableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("client_id")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("client_id")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build())
    }

    private fun createDynamoKeysTable() {
        dynamoDbClient.createTable(CreateTableRequest.builder()
                .tableName(dynamoKeysTableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("master_key_id")
                        .keyType(KeyType.HASH)
                        .build(),
                        KeySchemaElement.builder()
                                .attributeName("key_id")
                                .keyType(KeyType.RANGE)
                                .build())
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("master_key_id")
                        .attributeType(ScalarAttributeType.S)
                        .build(),
                        AttributeDefinition.builder()
                                .attributeName("key_id")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build());
    }

    private fun createDynamoMailVerificationTicketTable() {
        dynamoDbClient.createTable(CreateTableRequest.builder()
                .tableName(dynamoMailVerificationTicketTableName)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("ticket")
                        .keyType(KeyType.HASH)
                        .build()
                )
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("ticket")
                        .attributeType(ScalarAttributeType.S)
                        .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build());
    }

}