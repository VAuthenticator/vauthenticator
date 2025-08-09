package com.vauthenticator.server.role.adapter.jdbc

import com.vauthenticator.server.role.domain.Group
import com.vauthenticator.server.role.domain.GroupRepository
import com.vauthenticator.server.role.domain.GroupWitRoles
import com.vauthenticator.server.role.domain.Role
import com.vauthenticator.server.role.domain.RoleRepository
import com.vauthenticator.server.support.JdbcUtils.initRoleTestsInDB
import com.vauthenticator.server.support.JdbcUtils.jdbcClient
import com.vauthenticator.server.support.JdbcUtils.jdbcTemplate
import com.vauthenticator.server.support.JdbcUtils.resetDb
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JdbcGroupRepositoryTest {

    lateinit var uut: GroupRepository
    lateinit var roleRepository: RoleRepository

    @BeforeEach
    fun setUp() {
        uut = JdbcGroupRepository(jdbcClient)
        roleRepository = JdbcRoleRepository(jdbcTemplate, emptyList())
        resetDatabase()
    }

    fun resetDatabase() {
        resetDb()
        initRoleTestsInDB()
        roleRepository.save(Role("a_role_name", "a_role_description"))
        roleRepository.save(Role("another_role_name", "another_role_description"))
    }

    @Test
    fun `save a new group`() {
        uut.save(Group("a_group", "a description"))
        val  actual = uut.loadFor("a_group")
        val expected = GroupWitRoles(group = Group("a_group", "a description"), roles = emptyList())

        assertEquals(expected, actual)
    }

    @Test
    fun `add roles to a new group`() {
        uut.save(Group("a_group", "a description"))
        uut.roleAssociation("a_group", "a_role_name", "another_role_name")
        val actual = uut.loadFor("a_group")

        val expected = GroupWitRoles(
            group = Group("a_group", "a description"),
            roles = listOf(
                Role("a_role_name", "a_role_description"),
                Role("another_role_name", "another_role_description"),
            )
        )

        assertEquals(expected, actual)
    }

}