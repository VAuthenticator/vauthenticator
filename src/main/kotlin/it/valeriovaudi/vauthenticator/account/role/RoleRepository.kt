package it.valeriovaudi.vauthenticator.account.role

import it.valeriovaudi.vauthenticator.account.role.DynamoDbRoleMapper.deleteRoleRequestFor
import it.valeriovaudi.vauthenticator.account.role.DynamoDbRoleMapper.findAllRequestFor
import it.valeriovaudi.vauthenticator.account.role.DynamoDbRoleMapper.saveRoleRequestFor
import it.valeriovaudi.vauthenticator.account.role.DynamoDbRoleMapper.roleFor
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

class DynamoDbRoleRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val tableName: String
) : RoleRepository {
    override fun findAll() =
        dynamoDbClient.scan(findAllRequestFor(tableName))
            .items()
            .map(::roleFor)

    override fun save(role: Role) =
        dynamoDbClient.putItem(saveRoleRequestFor(role, tableName))
            .let { }

    override fun delete(roleName: String) =
        dynamoDbClient.deleteItem(deleteRoleRequestFor(roleName, tableName))
            .let { }

}

object DynamoDbRoleMapper {
    fun findAllRequestFor(tableName: String): ScanRequest =
        ScanRequest.builder()
            .tableName(tableName)
            .build()

    fun saveRoleRequestFor(role: Role, tableName: String): PutItemRequest = PutItemRequest.builder()
        .tableName(tableName)
        .item(
            mutableMapOf(
                "role_name" to AttributeValue.builder().s(role.name).build(),
                "description" to AttributeValue.builder().s(role.description).build()
            )
        )
        .build()


    fun deleteRoleRequestFor(roleName: String, tableName: String): DeleteItemRequest = DeleteItemRequest.builder()
        .tableName(tableName)
        .key(
            mutableMapOf(
                "role_name" to AttributeValue.builder().s(roleName).build()
            )
        )
        .build()

    fun roleFor(it: MutableMap<String, AttributeValue>) =
        Role(
            it["role_name"]?.s()!!,
            it["description"]?.s().orEmpty()
        )

}