package it.valeriovaudi.vauthenticator.keys

interface KeyRepository {
    fun createKeyFrom(masterKid: MasterKid, keyType: KeyType = KeyType.ASYMMETRIC, keyPurpose: KeyPurpose = KeyPurpose.TOKEN_SIGNATURE): Kid
    fun deleteKeyFor(masterKid: MasterKid, kid: Kid)
    fun tokenSignatureKeys(): Keys
    fun keyFor(kid: Kid): Key
}

interface KeyDecrypter {
    fun decryptKey(encrypted: String): String

}

interface KeyGenerator {
    fun dataKeyPairFor(masterKid: MasterKid) : DataKey
    fun dataKeyFor(masterKid: MasterKid): DataKey
}