package com.vauthenticator.server.keys.adapter.java

import com.vauthenticator.server.keys.domain.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!kms")
class KeyInitJob(
    @Value("\${key.master-key}") private val maserKid: String,
    private val keyStorage: KeyStorage,
    private val keyRepository: KeyRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {

        if (keyStorage.signatureKeys().keys.isEmpty()) {
            val kid = keyRepository.createKeyFrom(
                masterKid = MasterKid(maserKid),
                keyPurpose = KeyPurpose.SIGNATURE,
                keyType = KeyType.ASYMMETRIC,
            )
            println(kid)
        }

    }

}