package com.vauthenticator.server.keys.adapter.jdbc

import com.vauthenticator.server.keys.domain.*
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import java.time.Duration

class JdbcKeyStorage(private val jdbcTemplate: JdbcTemplate) : KeyStorage {

    private val logger = LoggerFactory.getLogger(JdbcKeyStorage::class.java)

    override fun store(
        masterKid: MasterKid,
        kid: Kid,
        dataKey: DataKey,
        keyType: KeyType,
        keyPurpose: KeyPurpose
    ) {
        jdbcTemplate.update(
            """
                INSERT INTO KEY (
                    key_id
                    master_key_id,
                    key_purpose,
                    key_type,
                    encrypted_private_key,
                    public_key,
                    enabled,
                    key_expiration_date_timestamp
                    ) VALUES (?,?,?,?,?,?,?,?)
                """.trimIndent(),
            kid.content(),
            masterKid.content(),
            dataKey.encryptedPrivateKeyAsString(),
            dataKey.publicKeyAsString(),
            keyPurpose.name,
            true,
            Duration.ofSeconds(0).toSeconds()
        )
    }

    override fun signatureKeys(): Keys {
        TODO()
    }

    override fun findOne(kid: Kid, keyPurpose: KeyPurpose): Key {
        return  jdbcTemplate.query(
            """
            SELECT * FROM KEY WHERE key_id = ? AND key_purpose = ?;
        """.trimIndent(), { rs, _ ->
                Key(
                    kid = Kid(rs.getString("key_id")),
                    masterKid = MasterKid(rs.getString("master_key_id")),
                    keyPurpose = KeyPurpose.valueOf(rs.getString("key_purpose")),
                    enabled = rs.getBoolean("enabled"),
                    type = KeyType.valueOf(rs.getString("key_type")),
                    expirationDateTimestamp = rs.getLong("key_expiration_date_timestamp"),
                    dataKey = DataKey.from(
                        rs.getString("encrypted_private_key"),
                        rs.getString("public_key"),
                    )
                    )
            },
            kid.content(), keyPurpose.name
        ).first()
    }

    override fun justDeleteKey(kid: Kid, keyPurpose: KeyPurpose) {
        TODO()
    }

    override fun keyDeleteJodPlannedFor(kid: Kid, ttl: Duration, keyPurpose: KeyPurpose) {
        TODO()
    }

}