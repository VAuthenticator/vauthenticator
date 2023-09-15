package com.vauthenticator.server.account.changepassword

import java.security.Principal

class ChangePassword {
    fun resetPasswordFor(principal: Principal, request: ChangePasswordRequest): Unit {
        TODO()
    }
}


data class ChangePasswordRequest(val newPassword: String)
