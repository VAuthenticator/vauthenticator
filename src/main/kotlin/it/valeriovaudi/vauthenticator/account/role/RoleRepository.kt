package it.valeriovaudi.vauthenticator.account.role

import org.springframework.jdbc.core.JdbcTemplate
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

interface RoleRepository {

    fun findAll(): List<Role>
    fun save(role: Role)
    fun delete(role: String)

}

class JdbcRoleRepository(val jdbcTemplate: JdbcTemplate) : RoleRepository {
    override fun findAll() =
        jdbcTemplate.query("SELECT * FROM ROLE")
        { rs, _ -> Role(rs.getString("name"), rs.getString("description")) }


    override fun save(role: Role) =
        jdbcTemplate.update(
            "INSERT INTO ROLE (NAME, DESCRIPTION) VALUES (?, ?) ON CONFLICT (NAME) DO UPDATE SET DESCRIPTION=? ",
            role.name, role.description, role.description
        )
            .let { }

    override fun delete(roleName: String) =
        jdbcTemplate.update("DELETE FROM ROLE WHERE NAME=?", roleName)
            .let { }
}

class DynamoDbRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val tableName: String
) : RoleRepository {
    override fun findAll() =
        dynamoDbClient.scan(
            ScanRequest.builder()
                .tableName(tableName)
                .build()
        ).items()
            .map {
                Role(
                    it["role_name"]?.s()!!,
                    it["description"]?.s().orEmpty()
                )
            }

    override fun save(role: Role) =
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(tableName)
                .item(
                    mutableMapOf(
                        "role_name" to AttributeValue.builder().s(role.name).build(),
                        "description" to AttributeValue.builder().s(role.description).build()
                    )
                )
                .build()
        ).let {  }

    override fun delete(roleName: String) =
        TODO()
}