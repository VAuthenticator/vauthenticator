package it.valeriovaudi.vauthenticator.account

import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
class AccountRegistrationTest {


    private val sub = UUID.randomUUID().toString()
    private val account = AccountTestFixture.anAccount(sub)

    @Autowired
    lateinit var accountRegistration: AccountRegistration

    @Test
    fun `register a new user`() {
        accountRegistration.execute(account)
    }
}