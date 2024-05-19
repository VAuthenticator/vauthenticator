package com.vauthenticator.server.mfa.repository

import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaMethod
import java.util.*

interface MfaAccountMethodsRepository {

    fun findOne(email: String, mfaMfaMethod: MfaMethod): Optional<MfaAccountMethod>
    fun findAll(email: String): List<MfaAccountMethod>
    fun save(email: String, mfaMfaMethod: MfaMethod): MfaAccountMethod
}

