package com.vauthenticator.server.role.adapter.jdbc

import com.vauthenticator.server.role.domain.Group
import com.vauthenticator.server.role.domain.GroupRepository
import com.vauthenticator.server.role.domain.GroupWitRoles
import com.vauthenticator.server.role.domain.Role
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.transaction.annotation.Transactional


@Transactional
class JdbcGroupRepository(val jdbcClient: JdbcClient) : GroupRepository {

    @Transactional(readOnly = true)
    override fun loadFor(groupName: String): GroupWitRoles? {
        return jdbcClient.sql("SELECT * FROM GROUPS WHERE name=:groupName")
            .param("groupName", groupName)
            .query { rs, _ ->
                GroupWitRoles(
                    group = Group(rs.getString("name"), rs.getString("description")),
                    roles = roleAssociationFor(groupName)
                )
            }
            .list()
            .firstOrNull()
    }

    @Transactional(readOnly = true)
    override fun findAll(): List<Group> {
        return jdbcClient.sql("SELECT * FROM GROUPS")
            .query { rs, _ -> Group(rs.getString("name"), rs.getString("description")) }
            .list()
    }

    override fun save(group: Group) {
        jdbcClient.sql("INSERT INTO GROUPS (name,description) VALUES (:groupName, :description);")
            .param("groupName", group.name)
            .param("description", group.description)
            .update()
    }

    override fun delete(groupName: String) {
        jdbcClient.sql("DELETE FROM GROUPS WHERE name=:groupName;")
            .param("groupName", groupName)
            .update()
    }

    override fun roleAssociation(groupName: String, vararg roleNames: String) {
        arrayOf(*roleNames)
            .forEach { roleName ->
                jdbcClient.sql("INSERT INTO GROUPS_ROLE (group_name,role_name) VALUES (:groupName, :roleName);")
                    .param("groupName", groupName)
                    .param("roleName", roleName)
                    .update()
            }

    }

    override fun roleDeAssociation(groupName: String, vararg roleNames: String) {
        arrayOf(*roleNames)
            .forEach { roleName ->
                jdbcClient.sql("DELETE FROM GROUPS_ROLE WHERE group_name=:groupName AND role_name=:roleName;")
                    .param("groupName", groupName)
                    .param("roleName", roleName)
                    .update()
            }
    }

    private fun roleAssociationFor(groupName: String): List<Role> {
        return jdbcClient.sql("SELECT * FROM GROUPS_ROLE as group_role join ROLE as role ON group_role.role_name=role.name WHERE group_role.group_name=:groupName;")
            .param("groupName", groupName)
            .query { rs, _ -> Role(rs.getString("name"), rs.getString("description")) }
            .list()
    }
}
