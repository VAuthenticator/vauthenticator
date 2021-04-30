package it.valeriovaudi.vauthenticator.role

import it.valeriovaudi.vauthenticator.support.TestingFixture.dataSource
import it.valeriovaudi.vauthenticator.support.TestingFixture.initRoleTests
import it.valeriovaudi.vauthenticator.support.TestingFixture.resetDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate

internal class JdbcRoleRepositoryTest {

    lateinit var roleRepository: RoleRepository

    @BeforeEach
    fun setUp() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        roleRepository = JdbcRoleRepository(jdbcTemplate)

        resetDatabase(jdbcTemplate)
        initRoleTests(jdbcTemplate)
    }

    @Test
    internal fun findAllRoles() {
        val actual = roleRepository.findAll()
        val expected: List<Role> = listOf(Role("a_role", "A_ROLE"))

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `save a new role`() {
        roleRepository.save(Role("another_role", "ANOTHER_ROLE"))
        val expected = roleRepository.findAll()
        val actual = listOf(Role("a_role", "A_ROLE"), Role("another_role", "ANOTHER_ROLE"))

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `save a new role again`() {
        roleRepository.save(Role("another_role", "ANOTHER_ROLE"))
        roleRepository.save(Role("another_role", "ANOTHER_ROLE AGAIN"))
        val expected = roleRepository.findAll()
        val actual = listOf(Role("a_role", "A_ROLE"), Role("another_role", "ANOTHER_ROLE AGAIN"))

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `delete a role`() {
        roleRepository.delete("a_role")
        val roles = roleRepository.findAll()

        Assertions.assertTrue { roles.isEmpty() }
    }
}