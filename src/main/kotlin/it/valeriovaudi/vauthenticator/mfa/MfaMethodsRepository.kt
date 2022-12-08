package it.valeriovaudi.vauthenticator.mfa

interface MfaMethodsRepository {

    fun findAll(email: String): List<MfaAccountMethod>
    fun store(email: String, mfaMfaMethod: MfaAccountMethod)
    fun delete(email: String)
}