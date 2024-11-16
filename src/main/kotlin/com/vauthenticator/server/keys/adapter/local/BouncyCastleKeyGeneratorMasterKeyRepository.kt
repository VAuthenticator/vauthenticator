package com.vauthenticator.server.keys.adapter.local

import com.vauthenticator.server.keys.domain.MasterKid
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


class BouncyCastleKeyGeneratorMasterKeyRepository(
    val storage: BouncyCastleKeyGeneratorMasterKeyStorage
) {

    fun maskerKeyFor(masterKeyId: MasterKid): String {
        return storage.content[masterKeyId.content()]!!
    }

}

@Profile("!kms")
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(BouncyCastleKeyGeneratorMasterKeyStorage::class)
class BouncyCastleKeyGeneratorMasterKeyRepositoryConfig {

}

@ConfigurationProperties(prefix = "key.master-key.storage")
data class BouncyCastleKeyGeneratorMasterKeyStorage(val content: Map<String, String>) {

}