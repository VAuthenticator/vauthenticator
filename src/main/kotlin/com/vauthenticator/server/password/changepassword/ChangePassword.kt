package com.vauthenticator.server.password.changepassword

import com.vauthenticator.server.account.AccountNotFoundException
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import java.security.Principal

class ChangePassword(
    private val passwordPolicy: PasswordPolicy,
    private val passwordEncoder: VAuthenticatorPasswordEncoder,
    private val accountRepository: AccountRepository
) {
    fun resetPasswordFor(principal: Principal, request: ChangePasswordRequest) {
        passwordPolicy.accept(request.newPassword)
        accountRepository.accountFor(principal.name)
            .map { account ->
                val newEncodedPassword = passwordEncoder.encode(request.newPassword)
                val updatedAccount = account.copy(password = newEncodedPassword)
                accountRepository.save(updatedAccount)
            }.orElseThrow { AccountNotFoundException("account not found") }
    }
}


data class ChangePasswordRequest(val newPassword: String)