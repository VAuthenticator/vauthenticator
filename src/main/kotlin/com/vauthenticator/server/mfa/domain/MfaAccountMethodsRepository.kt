package com.vauthenticator.server.mfa.domain

import java.util.*

interface MfaAccountMethodsRepository {

    fun findOne(userName: String, mfaMfaMethod: MfaMethod, mfaChannel: String): Optional<MfaAccountMethod>
    fun findAll(userName: String): List<MfaAccountMethod>
    fun save(userName: String, mfaMfaMethod: MfaMethod, mfaChannel: String, associated: Boolean): MfaAccountMethod
}

