package it.valeriovaudi.vauthenticator.keypair

import java.security.KeyPair

typealias Kid = String
typealias MasterKid = String
data class Keys (val keys : List<Key>)

data class Key (val keyPair : KeyPair, val masterKid : MasterKid, val kid : Kid, val enabled : Boolean)