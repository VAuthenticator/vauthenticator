package com.vauthenticator.server.keys.adapter.java

import com.vauthenticator.server.keys.domain.*
import org.slf4j.LoggerFactory
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

    val logger = LoggerFactory.getLogger(KeyInitJob::class.java)

    override fun run(args: ApplicationArguments) {

        if (keyStorage.signatureKeys().keys.isEmpty()) {
            val kid = keyRepository.createKeyFrom(
                masterKid = MasterKid(maserKid),
                keyPurpose = KeyPurpose.SIGNATURE,
                keyType = KeyType.ASYMMETRIC,
            )
            logger.info("Token Signature Key init job has been executed. Key ID generated: $kid")
        }

    }

}