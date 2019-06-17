package it.valeriovaudi.vauthenticator.repository

import org.hamcrest.core.Is
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import java.nio.file.Paths

class FileKeyRepositoryTest {

    @Test
    fun name() {
        val content = this::class.java.getResourceAsStream("/keystore/keystore.jks")
                .use { it.readAllBytes() }

        val keyStoreKeyFactory = KeyStoreKeyFactory(ByteArrayResource(content), "secret".toCharArray())
        val keyPair = keyStoreKeyFactory.getKeyPair("secret")
        val expectedAsPrivate = keyPair.private
        val expectedAsPublic = keyPair.public

        val fileKeyRepository = FileKeyRepository()

        val url = Paths.get("target/test-classes", "keystore/keystore.jks").toAbsolutePath().toString()
        val actual = fileKeyRepository.getKeyPair(url, "secret", "secret")

        assertThat(actual.private, Is.`is`(expectedAsPrivate))
        assertThat(actual.public, Is.`is`(expectedAsPublic))
    }
}