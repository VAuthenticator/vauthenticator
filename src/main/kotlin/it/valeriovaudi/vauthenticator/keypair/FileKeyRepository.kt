package it.valeriovaudi.vauthenticator.keypair

import it.valeriovaudi.vauthenticator.extentions.toByteArray
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory

class FileKeyRepository(val config: KeyPairConfig) : KeyRepository {

    override fun getKeyPair() = keyPairFor(keyStoreContent())

    private fun keyPairFor(it: ByteArray) = try {
        KeyStoreKeyFactory(ByteArrayResource(it), keystorePassword())
                .getKeyPair(config.keyStorePairAlias)
    } catch (e: Exception) {
        throw KeyPairNotFoundException(e.message!!)
    }

    private fun keystorePassword() = config.keyStorePassword.toCharArray()

    private fun keyStoreContent() = try {
        FileSystemResource(config.keyStorePath).inputStream.toByteArray()
    } catch (e: Exception) {
        ByteArray(0)
    }
}