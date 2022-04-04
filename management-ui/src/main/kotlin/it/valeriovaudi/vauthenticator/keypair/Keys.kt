package it.valeriovaudi.vauthenticator.keypair

import java.security.KeyPair

data class Keys (val keys : List<Key>)

data class Key (val keyPair : KeyPair, val masterKid : String, val kid : String, val enabled : Boolean)