package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.util.FileCopyUtils
import java.io.FileReader

class AccountCacheContentConverterTest {

    @Test
    fun `when a complete account from cache is loaded`() {
        val underTest = AccountCacheContentConverter(ObjectMapper())

        val cacheContent = testUseCase("aCompleteAccount.json")
        val actual = underTest.getObjectFromCacheContentFor(cacheContent)
        val expected = anAccount()
            .copy(
                birthDate = Date.isoDateFor("2023-01-01"),
                phone = Phone.phoneFor("+23 333 2323233")
            )

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `when a partial account from cache is loaded`() {
        val underTest = AccountCacheContentConverter(ObjectMapper())

        val cacheContent = testUseCase("aPartialAccount.json")
        val actual = underTest.getObjectFromCacheContentFor(cacheContent)
        val expected = anAccount()

        Assertions.assertEquals(expected, actual)
    }

    private fun testUseCase(fileName: String) =
        FileCopyUtils.copyToString(FileReader("src/test/resources/accounts/$fileName"))
}