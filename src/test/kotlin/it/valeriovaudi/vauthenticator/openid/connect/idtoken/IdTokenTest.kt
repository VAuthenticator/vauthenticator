package it.valeriovaudi.vauthenticator.openid.connect.idtoken

import it.valeriovaudi.vauthenticator.time.Clock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class IdTokenTest {

    @Mock
    lateinit var clock: Clock

    @Test
    fun `create a new id token`() {
        val clockTime: Long = 10000
        given(clock.nowInSeconds())
                .willReturn(clockTime)

        val actual = IdToken.createIdToken(iss = "AN_ISS", sub = "A_SUB",
                authentication = TestableOAuth2Authentication(),
                clock = clock)

        val expected = IdToken("USER_NAME",
                "AN_ISS", "A_SUB", "A_CLIENT_APPLICATION_ID",
                "A_NONCE",
                clockTime * 20,
                clockTime,
                clockTime)

        Assertions.assertEquals(actual, expected)
    }
}