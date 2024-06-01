package com.vauthenticator.server.account.repository

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.AccountMandatoryAction.RESET_PASSWORD
import com.vauthenticator.server.role.Role
import com.vauthenticator.server.role.RoleRepository
import com.vauthenticator.server.role.defaultRole
import com.vauthenticator.server.role.protectedRoleName
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

abstract class AbstractAccountRepositoryTest {

    private val role = Role("role", "description")
    private val anotherRole = Role("another_role", "description")
    private val account = anAccount(setOf(role, defaultRole))

    private lateinit var accountRepository: AccountRepository
    private lateinit var roleRepository: RoleRepository

    abstract fun initAccountRepository(roleRepository: RoleRepository): AccountRepository
    abstract fun initRoleRepository(): RoleRepository
    abstract fun resetDatabase()

    @BeforeEach
    fun setUp() {
        resetDatabase()

        roleRepository = initRoleRepository()
        accountRepository = initAccountRepository(roleRepository)


        roleRepository.save(Role(protectedRoleName, protectedRoleName))
        roleRepository.save(role)
        roleRepository.save(anotherRole)
    }

    @Test
    fun `find an account by email`() {
        accountRepository.save(account)
        val findByUsername: Account = accountRepository.accountFor(account.username).orElseThrow()

        assertEquals(account, findByUsername)
    }

    @Test
    fun `find an account by email with reset password as mandatory action`() {
        val account = account.copy(mandatoryAction = RESET_PASSWORD)
        accountRepository.save(account)
        val actual: Account = accountRepository.accountFor(account.username).orElseThrow()

        assertEquals(actual, account)
    }

    @Test
    fun `find an account by email after one master role delete`() {
        accountRepository.save(account)
        roleRepository.delete(role.name)

        val findByUsername: Account = accountRepository.accountFor(account.username).orElseThrow()

        assertEquals(findByUsername, account.copy(authorities = setOf(protectedRoleName)))
    }

    @Test
    fun `find an account by email when an account does not exist`() {
        accountRepository.save(account)
        val findByUsername: Optional<Account> = accountRepository.accountFor("not-existing-user-name")

        val empty: Optional<Account> = Optional.empty()
        assertEquals(findByUsername, empty)
    }

    @Test
    fun `save an account by email`() {
        accountRepository.save(account)

        val findByUsername: Account = accountRepository.accountFor(account.username).orElseThrow()
        assertEquals(findByUsername, account)

        val accountUpdated = account.copy(firstName = "A_NEW_FIRSTNAME", lastName = "A_NEW_LASTNAME")
        accountRepository.save(accountUpdated)

        val updatedFindByUsername = accountRepository.accountFor(account.username).orElseThrow()
        assertEquals(updatedFindByUsername, accountUpdated)
    }


    @Test
    fun `when overrides authorities to an accounts`() {
        val anotherAccount = account.copy(
            authorities = setOf("another_role")
        )
        accountRepository.save(account)
        accountRepository.save(anotherAccount)

        assertEquals(accountRepository.accountFor(account.username), Optional.of(anotherAccount))
    }

    @Test
    internal fun `when a new account is created`() {
        accountRepository.create(account)

        assertEquals(accountRepository.accountFor(account.username), Optional.of(account))
    }

    @Test
    internal fun `when a new account is created then once`() {
        assertThrows(AccountRegistrationException::class.java) {
            accountRepository.create(account)
            accountRepository.create(account)
        }
    }
}