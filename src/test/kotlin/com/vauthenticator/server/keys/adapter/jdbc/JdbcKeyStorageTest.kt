package com.vauthenticator.server.keys.adapter.jdbc

import com.vauthenticator.server.keys.adapter.AbstractKeyStorageTest
import com.vauthenticator.server.keys.domain.KeyPurpose
import com.vauthenticator.server.keys.domain.KeyStorage
import com.vauthenticator.server.keys.domain.Kid
import com.vauthenticator.server.support.JdbcUtils.jdbcTemplate
import com.vauthenticator.server.support.JdbcUtils.resetDb
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class JdbcKeyStorageTest  : AbstractKeyStorageTest() {

    override fun initKeyStorage(): KeyStorage = JdbcKeyStorage(jdbcTemplate)

    override fun resetDatabase() {
        resetDb()
    }

    override fun getActual(kid: Kid, keyPurpose: KeyPurpose): MutableMap<String, AttributeValue> {
        TODO("Not yet implemented")
    }


}