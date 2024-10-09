package com.vauthenticator.server.keys.domain

import com.vauthenticator.server.keys.DataKey
import com.vauthenticator.server.keys.MasterKid

interface KeyGenerator {
    fun dataKeyPairFor(masterKid: MasterKid): DataKey
    fun dataKeyFor(masterKid: MasterKid): DataKey
}

