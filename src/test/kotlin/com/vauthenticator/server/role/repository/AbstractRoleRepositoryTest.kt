package com.vauthenticator.server.role.repository

import com.vauthenticator.server.role.ProtectedRoleFromDeletionException
import com.vauthenticator.server.role.Role
import com.vauthenticator.server.role.RoleRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class AbstractRoleRepositoryTest {
    private lateinit var roleRepository: RoleRepository

    abstract fun initRoleRepository(): RoleRepository
    abstract fun resetDatabase()

    @BeforeEach
    fun setUp() {
        roleRepository = initRoleRepository()
        resetDatabase()
    }


    @Test
    fun findAllRoles() {
        val actual = roleRepository.findAll()
        val expected: List<Role> = listOf(Role("a_role", "A_ROLE"))

        assertEquals(expected, actual)
    }

    @Test
    fun `save a new role`() {
        roleRepository.save(Role("another_role", "ANOTHER_ROLE"))
        val expected = roleRepository.findAll().toSet()
        val actual = listOf(Role("a_role", "A_ROLE"), Role("another_role", "ANOTHER_ROLE")).toSet()

        assertEquals(expected, actual)
    }

    @Test
    fun `save a new role again`() {
        roleRepository.save(Role("another_role", "ANOTHER_ROLE"))
        roleRepository.save(Role("another_role", "ANOTHER_ROLE AGAIN"))
        val expected = roleRepository.findAll().toSet()
        val actual = listOf(Role("a_role", "A_ROLE"), Role("another_role", "ANOTHER_ROLE AGAIN")).toSet()

        assertEquals(expected, actual)
    }

    @Test
    fun `delete a role`() {
        roleRepository.delete("a_role")
        val roles = roleRepository.findAll()

        assertTrue { roles.isEmpty() }
    }

    @Test
    fun `when default role is attempted to be deleted`() {
        assertThrows(ProtectedRoleFromDeletionException::class.java) { roleRepository.delete("ROLE_USER") }
    }
}