package it.valeriovaudi.vauthenticator.repository

import java.security.KeyPair

interface KeyRepository {
    fun getKeyPair(keyStorePAth: String, password: String, alias: String): KeyPair
}