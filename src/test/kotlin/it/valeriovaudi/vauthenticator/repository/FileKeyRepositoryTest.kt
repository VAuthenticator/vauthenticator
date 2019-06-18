package it.valeriovaudi.vauthenticator.repository

import it.valeriovaudi.vauthenticator.repository.KeyPairFixture.errorPathKeyPairConfig
import it.valeriovaudi.vauthenticator.repository.KeyPairFixture.expectedFor
import it.valeriovaudi.vauthenticator.repository.KeyPairFixture.getFileContent
import it.valeriovaudi.vauthenticator.repository.KeyPairFixture.happyPathKeyPairConfig
import it.valeriovaudi.vauthenticator.repository.KeyPairMatcher.assertKeyOn
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class FileKeyRepositoryTest {

    @Rule
    @JvmField
    var exception: ExpectedException = ExpectedException.none()

    @Test
    fun `happy path`() {
        val content = getFileContent("/keystore/keystore.jks")
        val (expectedAsPrivate, expectedAsPublic) = expectedFor(content)

        val fileKeyRepository = FileKeyRepository(happyPathKeyPairConfig())

        val actual = fileKeyRepository.getKeyPair()

        assertKeyOn(actual, expectedAsPrivate, expectedAsPublic)
    }

    @Test
    fun `when keystore was not found`() {
        val fileKeyRepository = FileKeyRepository(errorPathKeyPairConfig())

        exception.expect(KeyPairNotFoundException::class.java)

        fileKeyRepository.getKeyPair()
    }

}