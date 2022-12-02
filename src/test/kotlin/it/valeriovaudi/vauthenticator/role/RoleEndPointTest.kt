package it.valeriovaudi.vauthenticator.role

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

//todo to migrate to mockk
@ExtendWith(MockitoExtension::class)
internal class RoleEndPointTest {

    lateinit var mokMvc: MockMvc

    @Mock
    lateinit var roleRepository: RoleRepository

    private val objectMapper = ObjectMapper()

    @BeforeEach
    internal fun setUp() {
        mokMvc = standaloneSetup(RoleEndPoint(roleRepository)).build()
    }

    @Test
    internal fun `find al roles`() {
        val roles = listOf(
                Role("a_role1", "A Role description"),
                Role("a_role2", "A Role description"),
                Role("a_role3", "A Role description")
        )

        given(roleRepository.findAll())
                .willReturn(roles)

        mokMvc.perform(get("/api/roles")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(roles)))

        verify(roleRepository).findAll()
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

    @Test
    internal fun `delete a new role`() {
        val role = "a_role"
        mokMvc.perform(delete("/api/roles/a_role"))
                .andExpect(status().isNoContent)

        verify(roleRepository).delete(role)
    }
}