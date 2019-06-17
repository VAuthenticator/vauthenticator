package it.valeriovaudi.vauthenticator.repository

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory

class FileKeyRepository(val config: FileKeyPairRepositoryConfig) : KeyRepository {
    override fun getKeyPair() =
            FileSystemResource(config.keyStorePath!!)
                    .inputStream.use { it.readAllBytes() }
                    .let { KeyStoreKeyFactory(ByteArrayResource(it), config.keyStorePassword!!.toCharArray()) }
                    .let { it.getKeyPair(config.keyStorePairAlias) }
}