package it.valeriovaudi.vauthenticator.jwk

import it.valeriovaudi.TestAdditionalConfiguration
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.getFileContent
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.keyPair
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.openidconnect.nonce.InMemoryNonceStore
import it.valeriovaudi.vauthenticator.openidconnect.nonce.NonceStore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@RunWith(SpringRunner::class)
@TestPropertySource(properties = ["key-store.keyStorePairAlias=ALIAS"])
@Import(TestAdditionalConfiguration::class)
@WebMvcTest(JwksEndPoint::class, secure = false)
class JwksEndPointTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var keyRepository: KeyRepository

    @MockBean
    lateinit var jwkFactory: JwkFactory

    @MockBean
    lateinit var nonceStore: NonceStore

    @Test
    fun `happy path`() {
        val content = getFileContent("/keystore/keystore.jks")
        val keyPair = keyPair(content)

        given(keyRepository.getKeyPair())
                .willReturn(keyPair)

        given(jwkFactory.createJwks(keyPair, "ALIAS"))
                .willReturn(Jwk())

        mockMvc.perform(MockMvcRequestBuilders.get("/.well-known/jwks.json"))
                .andExpect(status().isOk)

        verify(keyRepository).getKeyPair()
        verify(jwkFactory).createJwks(keyPair, "ALIAS")
    }
}