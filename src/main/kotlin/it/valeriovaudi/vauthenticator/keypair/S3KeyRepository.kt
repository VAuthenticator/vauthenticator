package it.valeriovaudi.vauthenticator.keypair

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.S3Object
import it.valeriovaudi.vauthenticator.toByteArray
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import java.security.KeyPair

class S3KeyRepository(private val keyPairConfig: KeyPairConfig,
                      private val s3Config: S3Config,
                      private val s3client: AmazonS3) : KeyRepository {

    override fun getKeyPair(): KeyPair {
        val content = contentFor(s3Object())

        val password = passwordFor(keyPairConfig)
        val keyStoreKeyFactory = keyStoreKeyFactoryFor(content, password)

        return keyPairWith(keyStoreKeyFactory)
    }

    private fun keyPairWith(keyStoreKeyFactory: KeyStoreKeyFactory) =
            keyStoreKeyFactory.getKeyPair(keyPairConfig.keyStorePairAlias)

    private fun keyStoreKeyFactoryFor(content: ByteArray, password: CharArray) =
            KeyStoreKeyFactory(ByteArrayResource(content), password)

    private fun passwordFor(keyPairConfig: KeyPairConfig) = keyPairConfig.keyStorePassword.toCharArray()


    private fun s3Object() = try {
        s3client.getObject(s3Config.bucketName, keyPairConfig.keyStorePath)
    } catch (e: Exception) {
        throw KeyPairNotFoundException(e.message!!)
    }

    private fun contentFor(s3Object: S3Object) = s3Object.objectContent.toByteArray()

}