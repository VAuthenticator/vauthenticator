package it.valeriovaudi.vauthenticator.mail

import it.valeriovaudi.vauthenticator.account.AccountTestFixture
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class SimpleMailPreRenderRuleTest {

    @Test
    internal fun `happy path`() {
        val rule = SimpleMailPreRenderRule()
        val account = AccountTestFixture.anAccount()
        val actual = rule.apply(account, mapOf("key" to "value"))
        val expected = mapOf(
                "enabled" to account.enabled,
                "username" to account.username,
                "authorities" to account.authorities,
                "email" to account.email,
                "firstName" to account.firstName,
                "lastName" to account.lastName,
                "birthDate" to account.birthDate.iso8601FormattedDate(),
                "phone" to account.phone.formattedPhone(),
                "key" to "value"
        )

        Assertions.assertEquals(expected, actual)
    }
}