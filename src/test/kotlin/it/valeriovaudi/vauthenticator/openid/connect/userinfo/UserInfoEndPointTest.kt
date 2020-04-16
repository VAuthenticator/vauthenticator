package it.valeriovaudi.vauthenticator.openid.connect.userinfo

import it.valeriovaudi.TestAdditionalConfiguration
import it.valeriovaudi.vauthenticator.account.AccountRepository
import it.valeriovaudi.vauthenticator.account.MongoUserRepository
import it.valeriovaudi.vauthenticator.openid.connect.nonce.NonceStore
import it.valeriovaudi.vauthenticator.support.WithMockOAuth2User
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@RunWith(SpringRunner::class)
@ActiveProfiles("web-tier")
@WebMvcTest(UserInfoEndPoint::class)
@Import(TestAdditionalConfiguration::class)
class UserInfoEndPointTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var nonceStore: NonceStore

    @MockBean
    lateinit var userInfoFactory: UserInfoFactory

    @MockBean
    lateinit var accountRepository: AccountRepository

    @MockBean
    lateinit var mongoUserRepository: MongoUserRepository

    @Test
    @WithMockOAuth2User("A_USER_NAME")
    fun `happy path`() {
        val objectMapper = ObjectMapper()
        val userInfo = UserInfo(sub = "", username = "A_USER_NAME", authorities = listOf("ROLE_USER"))

        val principal = SecurityContextHolder.getContext().authentication as JwtAuthenticationToken
        given(userInfoFactory.newUserInfo(principal))
                .willReturn(userInfo)

        mockMvc.perform(get("/user-info")
                .secure(true)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userInfo)))

    }
}