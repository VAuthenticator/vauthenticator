package com.vauthenticator.server.keys.adapter.local

import com.vauthenticator.server.keys.domain.MasterKid
val toSha256 = "CrZKwm8YWGN5xYeKlaC9vXUBAFFzKYsqfaOFSrrqQgA="

class BouncyCastleKeyGeneratorMasterKeyRepository {

    //TODO to improve
    fun maskerKeyFor(masterKeyId: MasterKid): String {
        return toSha256
    }

}