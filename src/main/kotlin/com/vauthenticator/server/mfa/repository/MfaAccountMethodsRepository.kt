package com.vauthenticator.server.mfa.repository

import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaMethod

interface MfaAccountMethodsRepository {

    fun findAll(email: String): Map<MfaMethod, MfaAccountMethod>
    fun save(email: String, mfaMfaMethod: MfaMethod): MfaAccountMethod
}

