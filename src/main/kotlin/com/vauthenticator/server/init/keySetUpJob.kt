package com.vauthenticator.server.init

import com.vauthenticator.server.keys.domain.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Service

@Service
class keySetUpJob(
    @Value("\${key.master-key}") private val maserKid: String,
    private val keyStorage: KeyStorage,
    private val keyRepository: KeyRepository
) : ApplicationRunner {

    val logger = LoggerFactory.getLogger(PermissionSetUpJob::class.java)

    override fun run(args: ApplicationArguments) {

        if (keyStorage.signatureKeys().keys.isEmpty()) {
            val firstKid = keyRepository.createKeyFrom(
                masterKid = MasterKid(maserKid),
                keyPurpose = KeyPurpose.SIGNATURE,
                keyType = KeyType.ASYMMETRIC,
            )
            val secondKid = keyRepository.createKeyFrom(
                masterKid = MasterKid(maserKid),
                keyPurpose = KeyPurpose.SIGNATURE,
                keyType = KeyType.ASYMMETRIC,
            )
            logger.info("Token Signature Key init job has been executed. Key IDs generated: $firstKid and $secondKid")
        }

    }

}