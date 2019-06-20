package it.valeriovaudi.vauthenticator.keypair

import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.errorPathKeyPairConfig
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.expectedFor
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.getFileContent
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.happyPathKeyPairConfig
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.s3Object
import it.valeriovaudi.vauthenticator.keypair.KeyPairMatcher.assertKeyOn
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class S3KeyRepositoryTest {

    @Rule
    @JvmField
    var exception: ExpectedException = ExpectedException.none()

    @Mock
    lateinit var s3client: AmazonS3

    companion object {
        val s3Config = S3Config(bucketName = "A_BUCKET")
    }

    @Test
    fun `happy path`() {
        val content = getFileContent("/keystore/keystore.jks")
        val (expectedAsPrivate, expectedAsPublic) = expectedFor(content)


        val s3KeyRepository = S3KeyRepository(happyPathKeyPairConfig(), s3Config, s3client)

        given(s3client.getObject("A_BUCKET", happyPathKeyPairConfig().keyStorePath))
                .willReturn(s3Object())

        val actual = s3KeyRepository.getKeyPair()

        assertKeyOn(actual, expectedAsPrivate, expectedAsPublic)
    }


    @Test
    fun `when keypair is not on AWS`() {
        val keyPairConfig = errorPathKeyPairConfig()
        val s3KeyRepository = S3KeyRepository(keyPairConfig, s3Config, s3client)

        given(s3client.getObject("A_BUCKET", keyPairConfig.keyStorePath))
                .willThrow(SdkClientException("Client error"))

        exception.expect(KeyPairNotFoundException::class.java)

        s3KeyRepository.getKeyPair()
    }
}