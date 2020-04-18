package it.valeriovaudi.vauthenticator.web.controller

import it.valeriovaudi.vauthenticator.openid.connect.nonce.NonceStore
import it.valeriovaudi.vauthenticator.userdetails.AccountUserDetailsService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@RunWith(SpringRunner::class)
@WebMvcTest(LoginPageController::class)
class LoginPageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var nonceStore: NonceStore

    @MockBean
    lateinit var redisTemplate: RedisTemplate<*, *>

    @MockBean
    lateinit var jwtDecoder: JwtDecoder

    @MockBean
    lateinit var accountUserDetailsService: AccountUserDetailsService

    @Test
    fun `happy path`() {
        mockMvc.perform(get("/singin"))
                .andExpect(status().isOk)
                .andExpect(view().name("login/singin"))


    }
}