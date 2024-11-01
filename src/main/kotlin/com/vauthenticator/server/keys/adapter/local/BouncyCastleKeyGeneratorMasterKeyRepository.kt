package com.vauthenticator.server.keys.adapter.local

import com.vauthenticator.server.extentions.toSha256
import com.vauthenticator.server.keys.domain.MasterKid
val toSha256 = "secret".toSha256()

class BouncyCastleKeyGeneratorMasterKeyRepository {

    //TODO to improve
    fun maskerKeyFor(masterKeyId: MasterKid): String {
        return toSha256
    }

}