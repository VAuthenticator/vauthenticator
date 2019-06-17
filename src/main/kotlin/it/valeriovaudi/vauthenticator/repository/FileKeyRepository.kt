package it.valeriovaudi.vauthenticator.repository

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory

class FileKeyRepository(val config: FileKeyPairRepositoryConfig) : KeyRepository {

    override fun getKeyPair() = keyPairFor(keyStoreContent())

    private fun keyPairFor(it: ByteArray) =
            KeyStoreKeyFactory(ByteArrayResource(it), keystorePassword())
                    .getKeyPair(config.keyStorePairAlias)

    private fun keystorePassword() = config.keyStorePassword!!.toCharArray()

    private fun keyStoreContent() = try {
        FileSystemResource(config.keyStorePath!!)
                .inputStream.use { it.readAllBytes() }
    } catch (e: Exception) {
        ByteArray(0)
    }
}