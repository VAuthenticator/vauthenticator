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
import org.junit.jupiter.api.assertNull
import kotlin.test.assertTrue

private const val A_GROUP = "a_group"
private const val ANOTHER_GROUP = "another_group"

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
    fun `when save a new group`() {
        uut.save(Group(A_GROUP, "a description"))
        val actual = uut.loadFor(A_GROUP)
        val expected = GroupWitRoles(group = Group(A_GROUP, "a description"), roles = emptyList())

        assertEquals(expected, actual)
    }

    @Test
    fun `when add roles to a new group`() {
        uut.save(Group(A_GROUP, "a description"))
        uut.roleAssociation(A_GROUP, "a_role_name", "another_role_name")
        val actual = uut.loadFor(A_GROUP)

        val expected = GroupWitRoles(
            group = Group(A_GROUP, "a description"),
            roles = listOf(
                Role("a_role_name", "a_role_description"),
                Role("another_role_name", "another_role_description"),
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `when remove roles to a new group`() {
        uut.save(Group(A_GROUP, "a description"))
        uut.roleAssociation(A_GROUP, "a_role_name", "another_role_name")
        uut.roleDeAssociation(A_GROUP, "another_role_name")

        val actual = uut.loadFor(A_GROUP)

        val expected = GroupWitRoles(
            group = Group(A_GROUP, "a description"),
            roles = listOf(
                Role("a_role_name", "a_role_description"),
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `when delete a new group`() {
        uut.save(Group(A_GROUP, "a description"))
        uut.roleAssociation(A_GROUP, "a_role_name", "another_role_name")
        uut.delete(A_GROUP)

        val actual = uut.loadFor(A_GROUP)
        val listOfRows = jdbcClient.sql("SELECT * FROM GROUPS_ROLE WHERE group_name=:groupName;")
            .param("groupName", A_GROUP)
            .query()
            .listOfRows()

        assertNull(actual)
        assertTrue { listOfRows.isEmpty() }
    }

    @Test
    fun `when get all groups`() {
        uut.save(Group(A_GROUP, "a description"))
        uut.save(Group(ANOTHER_GROUP, "a description"))

        val actual = uut.findAll()
        assertEquals(
            listOf(
                Group(A_GROUP, "a description"),
                Group(ANOTHER_GROUP, "a description")
            ), actual
        )

    }

}