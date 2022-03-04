package it.valeriovaudi.vauthenticator.account.repository

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.AccountTestFixture
import it.valeriovaudi.vauthenticator.role.Role
import it.valeriovaudi.vauthenticator.support.TestingFixture
import it.valeriovaudi.vauthenticator.support.TestingFixture.dynamoAccountRoleTableName
import it.valeriovaudi.vauthenticator.support.TestingFixture.dynamoAccountTableName
import it.valeriovaudi.vauthenticator.support.TestingFixture.dynamoDbClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class DynamoDbAccountRepositoryTest {

    private val account = AccountTestFixture.anAccount(listOf(Role("role", "description")))
    private lateinit var accountRepository: DynamoDbAccountRepository

    @BeforeEach
    fun setUp() {
        accountRepository = DynamoDbAccountRepository(
                dynamoDbClient,
                dynamoAccountTableName,
                dynamoAccountRoleTableName
        )
    }

    @AfterEach
    fun tearDown() {
        TestingFixture.resetDatabase(dynamoDbClient)
    }


    @Test
    fun `find an account by email`() {
        accountRepository.save(account)
        val findByUsername: Account = accountRepository.accountFor(account.username).orElseThrow()

        Assertions.assertEquals(findByUsername, account)
    }

    @Test
    fun `find an account by email when an account does not exist`() {
        accountRepository.save(account)
        val findByUsername: Optional<Account> = accountRepository.accountFor("not-existing-user-name")

        val empty: Optional<Account> = Optional.empty()
        Assertions.assertEquals(findByUsername, empty)
    }

    @Test
    fun `save an account by email`() {
        accountRepository.save(account)

        val findByUsername: Account = accountRepository.accountFor(account.username).orElseThrow()
        Assertions.assertEquals(findByUsername, account)

        val accountUpdated = account.copy(firstName = "A_NEW_FIRSTNAME", lastName = "A_NEW_LASTNAME")
        accountRepository.save(accountUpdated)

        val updatedFindByUsername = accountRepository.accountFor(account.username).orElseThrow()
        Assertions.assertEquals(updatedFindByUsername, accountUpdated)
    }

    @Test
    fun `find all accounts`() {
        val anAccount = account.copy()
        val anotherAccount = account.copy(
                email = "anotheremail@domain.com",
                username = "anotheremail@domain.com",
                firstName = "A_NEW_FIRSTNAME",
                lastName = "A_NEW_LASTNAME"
        )
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        val findAll = accountRepository.findAll(true)
        Assertions.assertTrue(findAll.contains(anAccount))
        Assertions.assertTrue(findAll.contains(anotherAccount))
    }

    @Test
    fun `find all accounts without autorities`() {
        val anAccount = account.copy()
        val anotherAccount = account.copy(
                email = "anotheremail@domain.com",
                username = "anotheremail@domain.com",
                firstName = "A_NEW_FIRSTNAME",
                lastName = "A_NEW_LASTNAME"
        )
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        val findAll = accountRepository.findAll()
        Assertions.assertTrue(findAll.contains(anAccount))
        Assertions.assertTrue(findAll.contains(anotherAccount))
    }

    @Test
    fun `when overrides authorities to an accounts`() {
        val anAccount = account.copy()
        val anotherAccount = account.copy(
                authorities = listOf("A_ROLE")
        )
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        Assertions.assertEquals(accountRepository.findAll(), listOf(anotherAccount))
    }

    @Test
    internal fun `when a new account is created`() {
        val anAccount = account.copy()
        accountRepository.create(anAccount)

        Assertions.assertEquals(accountRepository.findAll(), listOf(anAccount))
    }

    @Test
    internal fun `when a new account is created then once`() {
        val anAccount = account.copy()

        Assertions.assertThrows(AccountRegistrationException::class.java) {
            accountRepository.create(anAccount)
            accountRepository.create(anAccount)
        }

    }
}