package it.valeriovaudi.vauthenticator.account

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File

@Testcontainers
class JdbcAccountRepositoryTest {

    companion object {
        @Container
        val container: DockerComposeContainer<*> = DockerComposeContainer<Nothing>(File("src/test/resources/docker-compose.yml"))
                .withExposedService("postgres_1", 5432)

    }

    private val account = AccountTestFixture.anAccount()
    lateinit var accountRepository: JdbcAccountRepository

    @BeforeEach
    fun setUp() {
        val serviceHost = container.getServiceHost("postgres_1", 5432)
        val servicePort = container.getServicePort("postgres_1", 5432)
        val dataSource = DataSourceBuilder.create()
                .url("jdbc:postgresql://$serviceHost:$servicePort/vauthenticator?user=root&password=root")
                .build()
        accountRepository = JdbcAccountRepository(JdbcTemplate(dataSource))
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

        Assertions.assertEquals(accountRepository.findAll(true),listOf(anAccount, anotherAccount))
    }

    @Test
    fun `find all accounts without autorities`() {
        val anAccount = account.copy()
        val anotherAccount = account.copy(email = "anotheremail@domail.com", username = "anotheremail@domail.com", firstName = "A_NEW_FIRSTNAME", lastName = "A_NEW_LASTNAME")
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        Assertions.assertEquals(accountRepository.findAll(),listOf(anAccount, anotherAccount))
    }

}