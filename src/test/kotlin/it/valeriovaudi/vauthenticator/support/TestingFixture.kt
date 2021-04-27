package it.valeriovaudi.vauthenticator.support

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest

object TestingFixture {
    val dynamoRoleTableName = System.getenv("STAGING_DYNAMO_DB_ROLE_TABLE_NAME")

    val postGresHost = System.getProperty("test.database.host", "localhost")
    val postGresPort = System.getProperty("test.database.port", "35432")

    val dataSource = DataSourceBuilder.create()
        .url("jdbc:postgresql://$postGresHost:$postGresPort/vauthenticator?user=root&password=root")
        .build()

    fun initClientApplicationTests(jdbcTemplate: JdbcTemplate) {
        jdbcTemplate.execute("INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('client_id', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri','A_FEDERATION')");
        jdbcTemplate.execute("INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('federated_client_id1', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri','ANOTHER_FEDERATION')");
        jdbcTemplate.execute("INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('federated_client_id2', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri','ANOTHER_FEDERATION')");
        jdbcTemplate.execute("INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove, post_logout_redirect_uris, logout_uris, federation) VALUES ('A_CLIENT_APPLICATION_ID', 'oauth2-resource', 'secret', 'openid,profile,email', 'password', 'http://an_uri', 'AN_AUTHORITY', 10, 10, null, 'true', 'http://an_uri', 'http://an_uri123','ANOTHER_FEDERATION')");
    }

    fun initRoleTests(jdbcTemplate: JdbcTemplate) {
        jdbcTemplate.execute("INSERT INTO role (name, description) VALUES ('a_role','A_ROLE')")
    }

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

    fun resetDatabase(jdbcTemplate: JdbcTemplate) {
        jdbcTemplate.execute("TRUNCATE oauth_client_details")
        jdbcTemplate.execute("TRUNCATE ACCOUNT CASCADE")
        jdbcTemplate.execute("TRUNCATE ROLE CASCADE")
        jdbcTemplate.execute("TRUNCATE ACCOUNT_ROLE CASCADE")
    }

    fun resetDatabase(roleRepository: DynamoDbClient) {
        println("dynamoRoleTableName $dynamoRoleTableName")
        val scanRequest = ScanRequest.builder()
            .tableName(dynamoRoleTableName)
            .attributesToGet("role_name")
            .build()

        roleRepository.scan(scanRequest)
            .items()
            .forEach {
                val deleteItemRequest = DeleteItemRequest.builder().tableName(dynamoRoleTableName)
                    .key(mutableMapOf("role_name" to AttributeValue.builder().s(it["role_name"]?.s()).build()))
                    .build()

                roleRepository.deleteItem(deleteItemRequest)
            }
    }
}