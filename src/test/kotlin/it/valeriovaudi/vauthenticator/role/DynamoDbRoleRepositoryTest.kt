package it.valeriovaudi.vauthenticator.role

import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoDbClient
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoRoleTableName
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.initRoleTests
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.resetDatabase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DynamoDbRoleRepositoryTest {
    lateinit var roleRepository: RoleRepository

    @BeforeEach
    fun setUp() {
        roleRepository = DynamoDbRoleRepository(dynamoDbClient, dynamoRoleTableName)
        initRoleTests(dynamoDbClient)
    }

    @AfterEach
    fun tearDown() {
        resetDatabase(dynamoDbClient)
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
        val expected = roleRepository.findAll().toSet()
        val actual = listOf(Role("a_role", "A_ROLE"), Role("another_role", "ANOTHER_ROLE")).toSet()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `save a new role again`() {
        roleRepository.save(Role("another_role", "ANOTHER_ROLE"))
        roleRepository.save(Role("another_role", "ANOTHER_ROLE AGAIN"))
        val expected = roleRepository.findAll().toSet()
        val actual = listOf(Role("a_role", "A_ROLE"), Role("another_role", "ANOTHER_ROLE AGAIN")).toSet()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `delete a role`() {
        roleRepository.delete("a_role")
        val roles = roleRepository.findAll()

        Assertions.assertTrue { roles.isEmpty() }
    }
}