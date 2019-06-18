package it.valeriovaudi.vauthenticator.repository

import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.S3Object
import org.hamcrest.core.Is
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.core.io.ByteArrayResource
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class S3KeyRepositoryTest {

    @Rule
    @JvmField
    var exception: ExpectedException = ExpectedException.none()

    @Mock
    lateinit var s3client: AmazonS3

    @Test
    fun `happy path`() {
        val content = getFileContent("/keystore/keystore.jks")
        val (expectedAsPrivate, expectedAsPublic) = expected(content)

        val s3KeyRepository = S3KeyRepository(FileKeyPairRepositoryConfig(keyStorePath = "/keystore/keystore.jks", keyStorePairAlias = "secret", keyStorePassword = "secret"),
                S3KeyPairRepositoryConfig(bucketName = "A_BUCKET"), s3client)

        given(s3client.getObject("A_BUCKET", "/keystore/keystore.jks"))
                .willReturn(s3Object())

        val actual = s3KeyRepository.getKeyPair()

        assertThat(actual.private, Is.`is`(expectedAsPrivate))
        assertThat(actual.public, Is.`is`(expectedAsPublic))
    }

    @Test
    fun `when keypair is not on AWS`() {
        val s3KeyRepository = S3KeyRepository(FileKeyPairRepositoryConfig(),
                S3KeyPairRepositoryConfig(bucketName = "A_BUCKET"), s3client)

        given(s3client.getObject("A_BUCKET", ""))
                .willThrow(SdkClientException("Client error"))

        exception.expect(KeyPairNotFoundException::class.java)

        s3KeyRepository.getKeyPair()
    }


    private fun s3Object(): S3Object {
        val s3Object = S3Object()
        s3Object.setObjectContent(getFileContent("/keystore/keystore.jks").inputStream())
        return s3Object
    }


    private fun expected(content: ByteArray): Pair<PrivateKey, PublicKey> {
        val keyStoreKeyFactory = KeyStoreKeyFactory(ByteArrayResource(content), "secret".toCharArray())
        val keyPair = keyStoreKeyFactory.getKeyPair("secret")
        val expectedAsPrivate = keyPair.private
        val expectedAsPublic = keyPair.public
        return Pair(expectedAsPrivate, expectedAsPublic)
    }

    private fun getFileContent(path: String) = this::class.java.getResourceAsStream(path)
            .use { Optional.ofNullable(it).map { it.readAllBytes() }.orElse(ByteArray(0)) }
}