package it.valeriovaudi.vauthenticator.security.login

import it.valeriovaudi.TestAdditionalConfiguration
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@RunWith(SpringRunner::class)
@Import(TestAdditionalConfiguration::class)
@WebMvcTest(LoginPageController::class)
class LoginPageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `happy path`() {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk)
                .andExpect(view().name("login"))
    }
}