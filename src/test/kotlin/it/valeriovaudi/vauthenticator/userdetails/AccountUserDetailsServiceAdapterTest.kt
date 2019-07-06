package it.valeriovaudi.vauthenticator.userdetails

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.hamcrest.core.Is
import org.junit.Assert.assertThat
import org.junit.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails

class AccountUserDetailsServiceAdapterTest {

    @Test
    fun `from json to userdetails`() {

        val objectMapper = ObjectMapper()
        objectMapper.registerModule(KotlinModule())
        val accountUserDetailsServiceAdapter = AccountUserDetailsServiceAdapter(objectMapper)

        val accountUserDetails = """
            {
                "username":"USER_NAME",
                "password":"A_PASSWORD",
                "authorities": [
                                "AN_AUTHORITY",
                                "ANOTHER_AUTHORITY"
                                ]
            }
        """.trimIndent()
        val actual = accountUserDetailsServiceAdapter.convert(accountUserDetails)

        val expected: UserDetails =
                User("USER_NAME",
                        "A_PASSWORD",
                        listOf(SimpleGrantedAuthority("AN_AUTHORITY"),
                                SimpleGrantedAuthority("ANOTHER_AUTHORITY")))

        assertThat(actual, Is.`is`(expected))
    }

    @Test(expected = NotParsableAccountDetails::class)
    fun `when json is umparsable`() {

        val objectMapper = ObjectMapper()
        objectMapper.registerModule(KotlinModule())
        val accountUserDetailsServiceAdapter = AccountUserDetailsServiceAdapter(objectMapper)

        val accountUserDetails = ""
        accountUserDetailsServiceAdapter.convert(accountUserDetails)
    }
}