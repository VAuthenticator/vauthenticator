package it.valeriovaudi.vauthenticator.repository

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
        val content = getContentFor(s3Object())

        val password = keyPairConfig.keyStorePassword.toCharArray()
        val keyStoreKeyFactory = KeyStoreKeyFactory(ByteArrayResource(content), password)

        return keyStoreKeyFactory.getKeyPair(keyPairConfig.keyStorePairAlias)
    }

    private fun s3Object() = try {
        s3client.getObject(s3Config.bucketName, keyPairConfig.keyStorePath)
    } catch (e: Exception) {
        throw KeyPairNotFoundException(e.message!!)
    }


    private fun getContentFor(s3Object: S3Object) = s3Object.objectContent.toByteArray()

}