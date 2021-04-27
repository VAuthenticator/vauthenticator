package it.valeriovaudi.vauthenticator.account.role

import org.springframework.jdbc.core.JdbcTemplate
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

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
            .let { Unit }

    override fun delete(roleName: String) =
        jdbcTemplate.update("DELETE FROM ROLE WHERE NAME=?", roleName)
            .let { Unit }
}

class DynamoDbRepository(dynamoDbClient: DynamoDbClient) : RoleRepository {
    override fun findAll() =
        TODO()

    override fun save(role: Role) =
        TODO()

    override fun delete(roleName: String) =
        TODO()
}