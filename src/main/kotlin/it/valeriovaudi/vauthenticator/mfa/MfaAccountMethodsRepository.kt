package it.valeriovaudi.vauthenticator.mfa

interface MfaAccountMethodsRepository {

    fun findAll(email: String): List<MfaAccountMethod>
    fun save(email: String, mfaMfaMethod: MfaMethod) : MfaAccountMethod
    fun delete(email: String)
}