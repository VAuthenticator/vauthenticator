package it.valeriovaudi.vauthenticator.keys

interface KeyRepository {
    fun createKeyFrom(
        masterKid: MasterKid,
        keyType: KeyType = KeyType.ASYMMETRIC,
        keyPurpose: KeyPurpose = KeyPurpose.SIGNATURE
    ): Kid

    fun deleteKeyFor(kid: Kid, keyPurpose: KeyPurpose)
    fun signatureKeys(): Keys
    fun keyFor(kid: Kid, mfa: KeyPurpose): Key
}

interface KeyDecrypter {
    fun decryptKey(encrypted: String): String
    fun decryptKeyAsByteArray(privateKey: String) : ByteArray
}

interface KeyGenerator {
    fun dataKeyPairFor(masterKid: MasterKid): DataKey
    fun dataKeyFor(masterKid: MasterKid): DataKey
}