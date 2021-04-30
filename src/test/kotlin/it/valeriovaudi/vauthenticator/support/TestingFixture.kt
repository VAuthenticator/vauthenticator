package it.valeriovaudi.vauthenticator.support

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.PlainHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import com.nimbusds.jwt.SignedJWT
import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import javax.sql.DataSource

object TestingFixture {

    val dynamoRoleTableName: String = System.getenv("STAGING_DYNAMO_DB_ROLE_TABLE_NAME")
    val dynamoAccountTableName: String = System.getenv("STAGING_DYNAMO_DB_ACCOUNT_TABLE_NAME")
    val dynamoAccountRoleTableName: String = System.getenv("STAGING_DYNAMO_DB_ACCOUNT_ROLE_TABLE_NAME")
    val dynamoClientApplicationTableName: String = System.getenv("STAGING_DYNAMO_DB_CLIENT_APPLICATION_TABLE_NAME")

    val postGresHost: String = System.getProperty("test.database.host", "localhost")
    val postGresPort: String = System.getProperty("test.database.port", "35432")

    val dynamoDbClient: DynamoDbClient = DynamoDbClient.builder()
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build()

    val dataSource: DataSource = DataSourceBuilder.create()
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