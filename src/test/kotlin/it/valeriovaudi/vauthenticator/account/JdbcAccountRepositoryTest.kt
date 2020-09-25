package it.valeriovaudi.vauthenticator.account

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.DockerComposeContainer
import java.io.File

class JdbcAccountRepositoryTest {

    companion object {
        @ClassRule
        @JvmField
        val container: DockerComposeContainer<*> = DockerComposeContainer<Nothing>(File("src/test/resources/docker-compose.yml"))
                .withExposedService("postgres_1", 5432)

    }

    private val account = AccountTestFixture.anAccount()
    lateinit var accountRepository: JdbcAccountRepository

    @Before
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

        assertThat(findByUsername, equalTo(account))
    }

    @Test
    fun `save an account by email`() {
        accountRepository.save(account)

        val findByUsername: Account = accountRepository.accountFor(account.username).orElseThrow()
        assertThat(findByUsername, equalTo(account))

        val accountUpdated = account.copy(firstName = "A_NEW_FIRSTNAME", lastName = "A_NEW_LASTNAME")
        accountRepository.save(accountUpdated)

        val updatedFindByUsername = accountRepository.accountFor(account.username).orElseThrow()
        assertThat(updatedFindByUsername, equalTo(accountUpdated))
    }

    @Test
    fun `find all accounts`() {
        val anAccount = account.copy()
        val anotherAccount = account.copy(email = "anotheremail@domail.com", username = "anotheremail@domail.com", firstName = "A_NEW_FIRSTNAME", lastName = "A_NEW_LASTNAME")
        accountRepository.save(anAccount)
        accountRepository.save(anotherAccount)

        assertThat(accountRepository.findAll(), equalTo(listOf(anAccount, anotherAccount)))
    }

}