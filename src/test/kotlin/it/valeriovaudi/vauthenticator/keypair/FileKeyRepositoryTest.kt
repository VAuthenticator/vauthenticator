package it.valeriovaudi.vauthenticator.keypair

import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.errorPathKeyPairConfig
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.expectedFor
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.getFileContent
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.happyPathKeyPairConfig
import it.valeriovaudi.vauthenticator.keypair.KeyPairMatcher.assertKeyOn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileKeyRepositoryTest {

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

        Assertions.assertThrows(KeyPairNotFoundException::class.java) {
            fileKeyRepository.getKeyPair()
        }
    }

}