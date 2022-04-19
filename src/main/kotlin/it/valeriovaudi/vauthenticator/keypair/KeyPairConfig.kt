package it.valeriovaudi.vauthenticator.keypair

data class KeyPairConfig(var keyStorePath: String = "",
                         var keyStorePassword: String = "",
                         var keyStorePairAlias: String = "")