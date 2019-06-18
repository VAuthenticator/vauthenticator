package it.valeriovaudi.vauthenticator.repository

data class KeyPairConfig(var keyStorePath: String = "",
                         var keyStorePassword: String = "",
                         var keyStorePairAlias: String = "")