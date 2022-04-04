package it.valeriovaudi.vauthenticator.admin

import it.valeriovaudi.vauthenticator.security.userdetails.AccountUserDetailsService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@WebMvcTest(AdminApplicationController::class)
class AdminApplicationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var redisTemplate: RedisTemplate<*, *>

    @MockBean
    lateinit var jwtDecoder: JwtDecoder

    @MockBean
    lateinit var accountUserDetailsService: AccountUserDetailsService

    @Test
    @WithMockUser(authorities = ["VAUTHENTICATOR_ADMIN"])
    fun `happy path`() {
        mockMvc.perform(get("/secure/admin/index"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.view().name("secure/admin"))
    }
}