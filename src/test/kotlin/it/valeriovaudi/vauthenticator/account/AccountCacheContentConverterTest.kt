package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.util.FileCopyUtils
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Paths

class AccountCacheContentConverterTest {
    val underTest = AccountCacheContentConverter(ObjectMapper())

    @Test
    fun `when a complete account from cache is loaded`() {
        val cacheContent = testUseCase("aCompleteAccount.json")
        val actual = underTest.getObjectFromCacheContentFor(cacheContent)
        val expected = aCompleteAccount()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `when a partial account from cache is loaded`() {
        val cacheContent = testUseCase("aPartialAccount.json")
        val actual = underTest.getObjectFromCacheContentFor(cacheContent)
        val expected = aPartialAccount()

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `when a complete account is made ready for the cache`() {
        val actual = underTest.loadableContentIntoCacheFor(aCompleteAccount())
        val expected = testUseCaseInASingleLine("aCompleteAccount.json")

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `when a partial account  is made ready for the cache`() {
        val actual = underTest.loadableContentIntoCacheFor(aPartialAccount())
        val expected = testUseCaseInASingleLine("aPartialAccount.json")

        Assertions.assertEquals(expected, actual)
    }

    private fun aCompleteAccount() = aPartialAccount()
        .copy(
            birthDate = Date.isoDateFor("2023-01-01"),
            phone = Phone.phoneFor("+23 333 2323233")
        )

    private fun aPartialAccount() = anAccount()

    private fun testUseCase(fileName: String) =
        FileCopyUtils.copyToString(FileReader("src/test/resources/accounts/$fileName"))

    private fun testUseCaseInASingleLine(fileName: String) =
        Files.readAllLines(Paths.get("src/test/resources/accounts/$fileName"))
            .joinToString("") { it.replace(": ", ":").trim() }

}