package it.valeriovaudi.vauthenticator.repository

data class FileKeyPairRepositoryConfig(var keyStorePath: String? = null,
                                       var keyStorePassword: String? = null,
                                       var keyStorePairAlias: String? = null)