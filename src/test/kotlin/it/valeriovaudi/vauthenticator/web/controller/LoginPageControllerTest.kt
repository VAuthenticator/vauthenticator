package it.valeriovaudi.vauthenticator.web.controller

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@RunWith(SpringRunner::class)
@WebMvcTest(LoginPageController::class, secure = false)
class LoginPageControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `happy path`() {
        mockMvc.perform(get("/singin"))
                .andExpect(status().isOk)
                .andExpect(view().name("login/singin"))


    }
}