package it.valeriovaudi.vauthenticator.account.role

import org.springframework.jdbc.core.JdbcTemplate

interface RoleRepository {

    fun findAll(): List<Role>
    fun sale(role: Role)
    fun delete(role: Role)

}

class JdbcRoleRepository(val jdbcTemplate: JdbcTemplate) : RoleRepository {
    override fun findAll() =
            jdbcTemplate.query("SELECT * FROM ROLE")
            { rs, _ -> Role(rs.getString("name"), rs.getString("description")) }


    override fun sale(role: Role) {
        TODO("Not yet implemented")
    }

    override fun delete(role: Role) {
        TODO("Not yet implemented")
    }

}