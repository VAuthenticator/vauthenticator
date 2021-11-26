package it.valeriovaudi.vauthenticator.keypair

import java.security.KeyPair

interface KeyRepository {
    fun getKeyPair(): KeyPair

    fun keys(): Keys = TODO()
}