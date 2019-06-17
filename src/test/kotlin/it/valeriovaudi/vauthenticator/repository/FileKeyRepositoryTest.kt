package it.valeriovaudi.vauthenticator.repository

import org.hamcrest.core.Is
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import java.nio.file.Paths
import java.security.PrivateKey
import java.security.PublicKey

class FileKeyRepositoryTest {

    @Test
    fun `happy path`() {
        val content = getFileContent()
        val (expectedAsPrivate, expectedAsPublic) = expected(content)

        val url = Paths.get("target/test-classes", "keystore/keystore.jks").toAbsolutePath().toString()
        val fileKeyRepository = FileKeyRepository(FileKeyPairRepositoryConfig(keyStorePath = url, keyStorePairAlias = "secret", keyStorePassword = "secret"))

        val actual = fileKeyRepository.getKeyPair()

        assertThat(actual.private, Is.`is`(expectedAsPrivate))
        assertThat(actual.public, Is.`is`(expectedAsPublic))
    }

    private fun expected(content: ByteArray): Pair<PrivateKey, PublicKey> {
        val keyStoreKeyFactory = KeyStoreKeyFactory(ByteArrayResource(content), "secret".toCharArray())
        val keyPair = keyStoreKeyFactory.getKeyPair("secret")
        val expectedAsPrivate = keyPair.private
        val expectedAsPublic = keyPair.public
        return Pair(expectedAsPrivate, expectedAsPublic)
    }

    private fun getFileContent() = this::class.java.getResourceAsStream("/keystore/keystore.jks")
            .use { it.readAllBytes() }
}

