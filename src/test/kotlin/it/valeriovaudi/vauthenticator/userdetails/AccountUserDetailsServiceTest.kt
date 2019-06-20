package it.valeriovaudi.vauthenticator.userdetails

import org.hamcrest.core.Is
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

@RunWith(MockitoJUnitRunner::class)
class AccountUserDetailsServiceTest {

    @Mock
    lateinit var logInRequestGateway: LogInRequestGateway

    @Test
    fun `happy path`() {
        val accountUserDetailsService = AccountUserDetailsService(logInRequestGateway)
        val user: UserDetails = User("A_USERNAME", "secret", emptyList())

        given(logInRequestGateway.getPrincipleByUserName("A_USERNAME"))
                .willReturn(user)

        val loadUserByUsername = accountUserDetailsService.loadUserByUsername("A_USERNAME")

        assertThat(loadUserByUsername, Is.`is`(user))
    }
}