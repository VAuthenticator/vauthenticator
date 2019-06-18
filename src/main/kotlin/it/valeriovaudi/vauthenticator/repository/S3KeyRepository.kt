package it.valeriovaudi.vauthenticator.repository

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.S3Object
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import java.io.IOException
import java.security.KeyPair

class S3KeyRepository(private val fileKeyPairRepositoryConfig: FileKeyPairRepositoryConfig,
                      private val s3KeyPairRepositoryConfig: S3KeyPairRepositoryConfig,
                      private val s3client: AmazonS3) : KeyRepository {

    override fun getKeyPair(): KeyPair {
        val content = getContentFor(s3Object())

        val password = fileKeyPairRepositoryConfig.keyStorePassword.toCharArray()
        val keyStoreKeyFactory = KeyStoreKeyFactory(ByteArrayResource(content), password)

        return keyStoreKeyFactory.getKeyPair(fileKeyPairRepositoryConfig.keyStorePairAlias)
    }

    private fun s3Object() = try {
        s3client.getObject(s3KeyPairRepositoryConfig.bucketName, fileKeyPairRepositoryConfig.keyStorePath)
    } catch (e: Exception) {
        throw KeyPairNotFoundException(e.message!!)
    }


    private fun getContentFor(`object`: S3Object): ByteArray {
        try {
            return `object`.objectContent.readAllBytes()
        } catch (e: IOException) {
        }

        return ByteArray(0)
    }

}