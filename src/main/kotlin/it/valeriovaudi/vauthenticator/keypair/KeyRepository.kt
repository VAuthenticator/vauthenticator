package it.valeriovaudi.vauthenticator.keypair

interface KeyRepository {
    fun  createKeyFrom(masterKid: MasterKid) : Kid
    fun deleteKeyFor(masterKid: MasterKid, kid: Kid)
    fun keys(): Keys
}