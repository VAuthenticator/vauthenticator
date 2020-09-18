package it.valeriovaudi.vauthenticator.account.role

import org.springframework.jdbc.core.JdbcTemplate

interface RoleRepository {

    fun findAll(): List<Role>
    fun save(role: Role)
    fun delete(role: Role)

}

class JdbcRoleRepository(val jdbcTemplate: JdbcTemplate) : RoleRepository {
    override fun findAll() =
            jdbcTemplate.query("SELECT * FROM ROLE")
            { rs, _ -> Role(rs.getString("name"), rs.getString("description")) }


    override fun save(role: Role) =
            jdbcTemplate.update("INSERT INTO ROLE (NAME, DESCRIPTION) VALUES (?, ?)", role.name, role.desctiption)
                    .let { Unit }

    override fun delete(role: Role) {
        TODO("Not yet implemented")
    }

}