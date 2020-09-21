package it.valeriovaudi.vauthenticator.account.role

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@ExtendWith(MockitoExtension::class )
internal class RoleEndPointTest {

    lateinit var mokMvc: MockMvc

    @Mock
    lateinit var roleRepository: RoleRepository

    val objectMapper = ObjectMapper()

    @BeforeEach
    internal fun setUp() {
        mokMvc = standaloneSetup(RoleEndPoint(roleRepository)).build()
    }

    @Test
    internal fun `save a new role`() {
        val role = Role("a_role", "A Role description")
        mokMvc.perform(put("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(role)))
                .andExpect(status().isNoContent)

        verify(roleRepository).save(role)
    }
}