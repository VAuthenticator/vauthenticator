package com.vauthenticator.server.keys.adapter.local

import com.vauthenticator.server.keys.domain.KeyPurpose
import com.vauthenticator.server.keys.domain.KeyRepository
import com.vauthenticator.server.keys.domain.KeyType
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!aws")
class KeyInitJob(private val keyRepository: KeyRepository) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val kid = keyRepository.createKeyFrom(
            masterKid = MasterKeyGenrator.aMasterKey,
            keyPurpose = KeyPurpose.SIGNATURE,
            keyType = KeyType.ASYMMETRIC,
        )
        println(kid)
    }

}