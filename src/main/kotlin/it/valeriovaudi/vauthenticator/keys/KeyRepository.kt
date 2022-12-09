package it.valeriovaudi.vauthenticator.keys

interface KeyRepository {
    fun createKeyFrom(masterKid: MasterKid, keyType: KeyType = KeyType.ASYMMETRIC): Kid
    fun deleteKeyFor(masterKid: MasterKid, kid: Kid)
    fun keys(): Keys
    fun keyFor(kid: Kid): Key
}

interface KeyDecrypter {
    fun decryptKey(encrypted: String): String

}

interface KeyGenerator {
    fun dataKeyPairFor(masterKid: MasterKid) : DataKey
    fun dataKeyFor(masterKid: MasterKid): DataKey
}