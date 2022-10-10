package it.valeriovaudi.vauthenticator.keypair

import org.junit.jupiter.api.Assertions
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory
import java.nio.file.Paths
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

object KeyPairFixture {

    fun expectedFor(content: ByteArray): Pair<PrivateKey, PublicKey> {
        val keyPair = keyPair(content)
        val expectedAsPrivate = keyPair.private
        val expectedAsPublic = keyPair.public
        return Pair(expectedAsPrivate, expectedAsPublic)
    }

    private fun keyPair(content: ByteArray): KeyPair {
        val keyStoreKeyFactory = KeyStoreKeyFactory(ByteArrayResource(content), "secret".toCharArray())
        val keyPair = keyStoreKeyFactory.getKeyPair("secret")
        return keyPair
    }

    fun getFileContent(path: String) = this::class.java.getResourceAsStream(path)
            .use { Optional.ofNullable(it).map { it.readAllBytes() }.orElse(ByteArray(0)) }

    fun happyPathKeyPairConfig(): KeyPairConfig {
        val url = Paths.get("target/test-classes", "keystore/keystore.jks").toAbsolutePath().toString()
        val happyPathConfig = KeyPairConfig(keyStorePath = url, keyStorePairAlias = "secret", keyStorePassword = "secret")
        return happyPathConfig
    }

    fun errorPathKeyPairConfig() =
            KeyPairConfig(keyStorePath = "file.jsk", keyStorePairAlias = "secret", keyStorePassword = "secret")

}

object KeyPairMatcher {

    fun assertKeyOn(actual: KeyPair, expectedAsPrivate: PrivateKey, expectedAsPublic: PublicKey) {
        Assertions.assertEquals(actual.private,expectedAsPrivate)
        Assertions.assertEquals(actual.public, expectedAsPublic)
    }
}