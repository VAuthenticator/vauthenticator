package com.vauthenticator.server.keys.adapter.java

import com.vauthenticator.server.extentions.encoder
import com.vauthenticator.server.keys.domain.DataKey
import com.vauthenticator.server.keys.domain.MasterKid
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.security.KeyPair

@ExtendWith(MockKExtension::class)
class JavaSecurityKeyGeneratorTest {

    @MockK
    lateinit var keyCryptographicOperations: KeyCryptographicOperations

    /*
    *
       val generateRSAKeyPair = keyCryptographicOperations.generateRSAKeyPair()
        return DataKey(
            keyCryptographicOperations.encryptKeyWith(masterKid, generateRSAKeyPair.private.encoded),
            Optional.empty()
        )
        * */
    @Test
    fun `when a new data key is created`() {
        val uut = JavaSecurityKeyGenerator(keyCryptographicOperations)
        val masterKid = MasterKid("A_MASTER_KEY")
        val keyPair = mockk<KeyPair>()
        val anEncryptedValueAsByteArray = "AN_ENCRYPTED_VALUE".toByteArray()

        every { keyCryptographicOperations.generateRSAKeyPair() } returns keyPair
        every { keyPair.private } returns mockk {
            every { keyPair.private.encoded } returns anEncryptedValueAsByteArray
        }
        every { keyCryptographicOperations.encryptKeyWith(masterKid, anEncryptedValueAsByteArray) } returns anEncryptedValueAsByteArray

        val actual = uut.dataKeyFor(masterKid)
        val expected = DataKey.from()
    }
}