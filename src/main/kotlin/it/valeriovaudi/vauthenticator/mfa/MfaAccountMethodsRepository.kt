package it.valeriovaudi.vauthenticator.mfa

interface MfaAccountMethodsRepository {

    fun findAll(email: String): List<MfaAccountMethod>
    fun save(email: String, mfaMfaMethod: MfaMethod) : MfaAccountMethod
    fun delete(email: String): Nothing = TODO()
}

class DynamoMfaAccountMethodsRepository : MfaAccountMethodsRepository {
    override fun findAll(email: String): List<MfaAccountMethod> {
        TODO("Not yet implemented")
    }

    override fun save(email: String, mfaMfaMethod: MfaMethod): MfaAccountMethod {
        TODO("Not yet implemented")
    }

}