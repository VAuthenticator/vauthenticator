package it.valeriovaudi.vauthenticator.keypair

import org.springframework.cache.annotation.Cacheable
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory
import org.springframework.web.client.RestTemplate
import java.security.KeyPair
import java.util.*

open class RestKeyRepository(
        private val restTemplates: RestTemplate,
        private val repositoryServiceUrl: String,
        private val registrationName: String,
        private val keyPairConfig: KeyPairConfig
) : KeyRepository {

    @Cacheable("keyPair")
    override fun getKeyPair(): KeyPair {
        val content = content()

        val password = passwordFor(keyPairConfig)
        val keyStoreKeyFactory = keyStoreKeyFactoryFor(content, password)

        return keyPairWith(keyStoreKeyFactory)
    }

    private fun content(): ByteArray =
            restTemplates.getForEntity(url(), ByteArray::class.java)
                    .let {
                        when (it.statusCode.is2xxSuccessful) {
                            true -> Optional.ofNullable(it.body).orElseThrow { KeyPairNotFoundException("key pair not found") }
                            false -> throw KeyPairNotFoundException("key pair not found")
                        }
                    }

    private fun keyPairWith(keyStoreKeyFactory: KeyStoreKeyFactory) =
            keyStoreKeyFactory.getKeyPair(keyPairConfig.keyStorePairAlias)

    private fun keyStoreKeyFactoryFor(content: ByteArray, password: CharArray) =
            KeyStoreKeyFactory(ByteArrayResource(content), password)

    private fun passwordFor(keyPairConfig: KeyPairConfig) = keyPairConfig.keyStorePassword.toCharArray()

    private fun url() =
            "$repositoryServiceUrl/documents/$registrationName?path=certificates&fileName=keystore&fileExt=jks"
}