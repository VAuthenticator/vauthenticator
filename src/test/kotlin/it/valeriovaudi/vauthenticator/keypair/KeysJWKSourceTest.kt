package it.valeriovaudi.vauthenticator.keypair

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

    @Test
    internal fun `when the context is loaded`() {
        val underTest = KeysJWKSource(keyRepository)

        every { keyRepository.keys() } returns Keys(emptyList())

        underTest.get(JWKSelector(JWKMatcher.Builder().build()), null)
    }
}