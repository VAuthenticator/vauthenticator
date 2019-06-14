package it.valeriovaudi.vauthenticator.config

data class KeyPairRepositoryConfigurationProperties(var keyStorePath: String = "",
                                                    var keyStorePassword: String = "",
                                                    var keyStorePairAlias: String = "")