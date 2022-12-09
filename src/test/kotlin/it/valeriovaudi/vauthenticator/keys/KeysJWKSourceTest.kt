package it.valeriovaudi.vauthenticator.keys

import com.nimbusds.jose.jwk.JWKMatcher
import com.nimbusds.jose.jwk.JWKSelector
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class KeysJWKSourceTest {

    @MockK
    private lateinit var keyRepository : KeyRepository
    @MockK
    private lateinit var keyDecrypter: KeyDecrypter

    @Test
    internal fun `when the context is loaded`() {
        val underTest = KeysJWKSource(keyDecrypter, keyRepository)

        every { keyRepository.tokenSignatureKeys() } returns Keys(emptyList())

        underTest.get(JWKSelector(JWKMatcher.Builder().build()), null)
    }
}