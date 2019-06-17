package it.valeriovaudi.vauthenticator.repository

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.FileSystemResource
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import java.security.KeyPair

class FileKeyRepository : KeyRepository {
    override fun getKeyPair(keyStorePath: String, password: String, alias: String): KeyPair =
            FileSystemResource(keyStorePath)
                    .inputStream.use { it.readAllBytes() }
                    .let { KeyStoreKeyFactory(ByteArrayResource(it), password.toCharArray()) }
                    .let { it.getKeyPair(alias) }
}