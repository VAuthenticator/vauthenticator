package com.vauthenticator.server.password

import org.springframework.security.crypto.password.PasswordEncoder

interface VAuthenticatorPasswordEncoder {
    fun encode(password: String): String
}

class BcryptVAuthenticatorPasswordEncoder(private val passwordEncoder: PasswordEncoder) :
    VAuthenticatorPasswordEncoder {

    override fun encode(password: String): String = passwordEncoder.encode(password)

}