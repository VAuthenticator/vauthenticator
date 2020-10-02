package it.valeriovaudi.vauthenticator.openid.connect.idtoken

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.AccountRepository
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.getFileContent
import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture.keyPair
import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import it.valeriovaudi.vauthenticator.time.Clock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.times
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.oauth2.common.OAuth2AccessToken
import java.util.*

@ExtendWith(MockitoExtension::class)
class IdTokenEnhancerTest {

    @Mock
    lateinit var keyRepository: KeyRepository

    @Mock
    lateinit var accountRepository: AccountRepository

    @Mock
    lateinit var clock: Clock

    @Test
    fun `when client application has openid as scope`() {
        val idTokenEnhancer = IdTokenEnhancer("AN_ISS", accountRepository, keyRepository, clock)

        val content = getFileContent("/keystore/keystore.jks")
        given(keyRepository.getKeyPair())
                .willReturn(keyPair(content = content))

        given(accountRepository.accountFor("USER_NAME"))
                .willReturn(Optional.of(Account(false, false, false, true, "", "", emptyList(), "", true, "", "")))
        val actual: OAuth2AccessToken = idTokenEnhancer.enhance(TestableDefaultOAuth2AccessToken(clientAppScope = setOf("openid")), TestableOAuth2Authentication())

        Assertions.assertNotNull(actual.additionalInformation["id_token"])

        verify(clock).nowInSeconds()
        verify(keyRepository).getKeyPair()
    }

    @Test
    fun `when client application does not has openid as scope`() {
        val idTokenEnhancer = IdTokenEnhancer("AN_ISS", accountRepository, keyRepository, clock)

        val actual: OAuth2AccessToken = idTokenEnhancer.enhance(TestableDefaultOAuth2AccessToken(), TestableOAuth2Authentication())

        Assertions.assertNull(actual.additionalInformation["id_token"])

        verify(clock, times(0)).nowInSeconds()
        verify(keyRepository, times(0)).getKeyPair()
    }
}