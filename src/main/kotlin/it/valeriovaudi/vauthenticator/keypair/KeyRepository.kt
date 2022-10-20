package it.valeriovaudi.vauthenticator.keypair

interface KeyRepository {
    fun createKeyFrom(masterKid: MasterKid) : Kid
    fun deleteKeyFor(kid: Kid)
    fun keys(): Keys
}