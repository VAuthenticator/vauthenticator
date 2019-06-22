package it.valeriovaudi.vauthenticator.openidconnect.idtoken

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.oauth2.common.OAuth2AccessToken

@RunWith(MockitoJUnitRunner::class)
class IdTokenEnhancerTest {

    @Test
    fun `when client application has openid as scope`() {
        val actual: OAuth2AccessToken = IdTokenEnhancer().enhance(TestableDefaultOAuth2AccessToken(), TestableOAuth2Authentication())

        assertNotNull(actual.additionalInformation["id_token"])
    }
}