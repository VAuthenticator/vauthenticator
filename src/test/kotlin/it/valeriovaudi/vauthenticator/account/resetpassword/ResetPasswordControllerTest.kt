package it.valeriovaudi.vauthenticator.account.resetpassword

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

internal class ResetPasswordControllerTest {
    lateinit var mokMvc: MockMvc

    @BeforeEach
    internal fun setUp() {
        mokMvc = MockMvcBuilders.standaloneSetup(ResetPasswordController())
                .build()
    }

    @Test
    internal fun `when the reset password page is shown`() {
        mokMvc.perform(get("/reset-password/{ticket}", "A_TICKET"))
                .andExpect(status().isOk)
                .andExpect(model().attribute("ticket", "A_TICKET"))
                .andExpect(view().name("account/reset-password/reset-password"))
    }
    @Test
    internal fun `when the successful reset password page is shown`() {
        mokMvc.perform(get("/reset-password/successful-password-reset"))
                .andExpect(status().isOk)
                .andExpect(view().name("account/reset-password/successful-password-reset"))
    }
}
