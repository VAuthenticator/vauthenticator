package com.vauthenticator.server.mfa.repository

import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaMethod
import java.util.*

//todo the interface has to take in account the enrolled method

interface MfaAccountMethodsRepository {

//    fun findOne(userName: String, mfaChannel : String): Optional<MfaAccountMethod>
    fun findOne(userName: String, mfaMfaMethod: MfaMethod, mfaChannel : String): Optional<MfaAccountMethod>
    fun findAll(userName: String): List<MfaAccountMethod>
    fun save(userName: String, mfaMfaMethod: MfaMethod, mfaChannel : String, associated : Boolean): MfaAccountMethod
}

