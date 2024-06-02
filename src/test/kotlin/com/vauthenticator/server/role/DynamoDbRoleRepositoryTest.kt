package com.vauthenticator.server.role

import com.vauthenticator.server.role.repository.dynamodb.DynamoDbRoleRepository
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDbClient
import com.vauthenticator.server.support.DynamoDbUtils.dynamoRoleTableName
import com.vauthenticator.server.support.DynamoDbUtils.initRoleTests
import com.vauthenticator.server.support.DynamoDbUtils.resetDynamoDb
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DynamoDbRoleRepositoryTest {
    private lateinit var roleRepository: RoleRepository

    @BeforeEach
    fun setUp() {
        roleRepository = DynamoDbRoleRepository(protectedRoleNames, dynamoDbClient, dynamoRoleTableName)
        resetDynamoDb(dynamoDbClient)
        initRoleTests(dynamoDbClient)
    }

    @Test
    internal fun findAllRoles() {
        val actual = roleRepository.findAll()
        val expected: List<Role> = listOf(Role("a_role", "A_ROLE"))

        assertEquals(expected, actual)
    }

    @Test
    internal fun `save a new role`() {
        roleRepository.save(Role("another_role", "ANOTHER_ROLE"))
        val expected = roleRepository.findAll().toSet()
        val actual = listOf(Role("a_role", "A_ROLE"), Role("another_role", "ANOTHER_ROLE")).toSet()

        assertEquals(expected, actual)
    }

    @Test
    internal fun `save a new role again`() {
        roleRepository.save(Role("another_role", "ANOTHER_ROLE"))
        roleRepository.save(Role("another_role", "ANOTHER_ROLE AGAIN"))
        val expected = roleRepository.findAll().toSet()
        val actual = listOf(Role("a_role", "A_ROLE"), Role("another_role", "ANOTHER_ROLE AGAIN")).toSet()

        assertEquals(expected, actual)
    }

    @Test
    internal fun `delete a role`() {
        roleRepository.delete("a_role")
        val roles = roleRepository.findAll()

        assertTrue { roles.isEmpty() }
    }

    @Test
    fun `when default role is attempted to be deleted`() {
        assertThrows(ProtectedRoleFromDeletionException::class.java) { roleRepository.delete("ROLE_USER") }
    }
}