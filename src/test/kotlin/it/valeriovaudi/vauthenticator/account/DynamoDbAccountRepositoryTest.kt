package it.valeriovaudi.vauthenticator.account

import it.valeriovaudi.vauthenticator.account.role.Role
import it.valeriovaudi.vauthenticator.support.TestingFixture
import it.valeriovaudi.vauthenticator.support.TestingFixture.dynamoAccountRoleTableName
import it.valeriovaudi.vauthenticator.support.TestingFixture.dynamoAccountTableName
import it.valeriovaudi.vauthenticator.support.TestingFixture.dynamoDbClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DynamoDbAccountRepositoryTest {

    private val account = AccountTestFixture.anAccount(listOf(Role("role", "description")))
    lateinit var accountRepository: DynamoDbAccountRepository

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
            email = "anotheremail@domail.com",
            username = "anotheremail@domail.com",
            firstName = "A_NEW_FIRSTNAME",
            lastName = "A_NEW_LASTNAME"
        )
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        Assertions.assertEquals(accountRepository.findAll(true), listOf(anAccount, anotherAccount))
    }

    @Test
    fun `find all accounts without autorities`() {
        val anAccount = account.copy()
        val anotherAccount = account.copy(
            email = "anotheremail@domail.com",
            username = "anotheremail@domail.com",
            firstName = "A_NEW_FIRSTNAME",
            lastName = "A_NEW_LASTNAME"
        )
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        Assertions.assertEquals(accountRepository.findAll(), listOf(anAccount, anotherAccount))
    }
}