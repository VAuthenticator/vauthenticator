package it.valeriovaudi.vauthenticator.repository

import java.security.KeyPair

interface KeyRepository {
    fun getKeyPair(): KeyPair
}