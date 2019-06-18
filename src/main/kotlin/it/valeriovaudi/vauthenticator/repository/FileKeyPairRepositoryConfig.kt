package it.valeriovaudi.vauthenticator.repository

data class FileKeyPairRepositoryConfig(var keyStorePath: String = "",
                                       var keyStorePassword: String = "",
                                       var keyStorePairAlias: String = "")