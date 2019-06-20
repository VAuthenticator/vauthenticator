package it.valeriovaudi.vauthenticator.keypair

import com.amazonaws.services.s3.model.S3Object
import org.hamcrest.core.Is
import org.junit.Assert
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import java.nio.file.Paths
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

object KeyPairFixture {

    fun expectedFor(content: ByteArray): Pair<PrivateKey, PublicKey> {
        val keyStoreKeyFactory = KeyStoreKeyFactory(ByteArrayResource(content), "secret".toCharArray())
        val keyPair = keyStoreKeyFactory.getKeyPair("secret")
        val expectedAsPrivate = keyPair.private
        val expectedAsPublic = keyPair.public
        return Pair(expectedAsPrivate, expectedAsPublic)
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

    fun s3Object(): S3Object {
        val s3Object = S3Object()
        s3Object.setObjectContent(getFileContent("/keystore/keystore.jks").inputStream())
        return s3Object
    }

}

object KeyPairMatcher {

    fun assertKeyOn(actual: KeyPair, expectedAsPrivate: PrivateKey, expectedAsPublic: PublicKey) {
        Assert.assertThat(actual.private, Is.`is`(expectedAsPrivate))
        Assert.assertThat(actual.public, Is.`is`(expectedAsPublic))
    }
}