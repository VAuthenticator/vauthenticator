package it.valeriovaudi.vauthenticator.account.database

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.AccountTestFixture
import it.valeriovaudi.vauthenticator.support.TestingFixture.dataSource
import it.valeriovaudi.vauthenticator.support.TestingFixture.resetDatabase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.JdbcTemplate

class JdbcAccountRepositoryTest {

    private val account = AccountTestFixture.anAccount()
    lateinit var accountRepository: JdbcAccountRepository

    @BeforeEach
    fun setUp() {
        val jdbcTemplate = JdbcTemplate(dataSource)
        resetDatabase(jdbcTemplate)
        accountRepository = JdbcAccountRepository(jdbcTemplate)
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
        val anotherAccount = account.copy(email = "anotheremail@domail.com", username = "anotheremail@domail.com", firstName = "A_NEW_FIRSTNAME", lastName = "A_NEW_LASTNAME")
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        Assertions.assertEquals(accountRepository.findAll(true), listOf(anAccount, anotherAccount))
    }

    @Test
    fun `find all accounts without autorities`() {
        val anAccount = account.copy()
        val anotherAccount = account.copy(email = "anotheremail@domail.com", username = "anotheremail@domail.com", firstName = "A_NEW_FIRSTNAME", lastName = "A_NEW_LASTNAME")
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        Assertions.assertEquals(accountRepository.findAll(), listOf(anAccount, anotherAccount))
    }

}