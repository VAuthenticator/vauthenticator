package it.valeriovaudi.vauthenticator.userdetails

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class AccountUserDetailsServiceAdapter(private val objectMapper: ObjectMapper) {
    fun convert(securityAccountDetails: String) =
            try {
                objectMapper.readValue(securityAccountDetails, SecurityAccountDetails::class.java)
                        .let { User(it.username, it.password, it.authorities.map { SimpleGrantedAuthority(it) }) }
            } catch (e: Exception) {
                throw NotParsableAccountDetails(e.message!!, e)
            }
}