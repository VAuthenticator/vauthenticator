package it.valeriovaudi.vauthenticator.account.role

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest
@ExtendWith(SpringExtension::class)
internal class RoleEndPointTest {

    @Autowired
    lateinit var mokMvc: MockMvc

    @MockBean
    lateinit var roleRepository: RoleRepository



}