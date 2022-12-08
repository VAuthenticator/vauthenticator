package it.valeriovaudi.vauthenticator.keys

interface KeyRepository {
    fun createKeyFrom(masterKid: MasterKid, keyType: KeyType = KeyType.ASYMMETRIC): Kid
    fun deleteKeyFor(masterKid: MasterKid, kid: Kid)
    fun keys(): Keys
}