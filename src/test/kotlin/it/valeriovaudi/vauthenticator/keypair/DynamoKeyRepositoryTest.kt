package it.valeriovaudi.vauthenticator.keypair

import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoDbClient
import it.valeriovaudi.vauthenticator.support.DatabaseUtils.dynamoKeysTableName
import it.valeriovaudi.vauthenticator.support.KeysUtils.aNewMasterKey
import it.valeriovaudi.vauthenticator.support.KeysUtils.kmsClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DynamoKeyRepositoryTest {


    lateinit var keyRepository: KeyRepository

    @BeforeEach
    internal fun setUp() {
        keyRepository = DynamoKeyRepository(dynamoKeysTableName, KmsKeyRepository(kmsClient), kmsClient, dynamoDbClient)
    }

    @Test
    internal fun `when create a new key`() {
        val aNewMasterKey = aNewMasterKey()

        val masterKid = aNewMasterKey.invoke()
        println("masterKid $masterKid")
        println("masterKid $masterKid")
        println("kid ${keyRepository.createKeyFrom(masterKid)}")
        println("kid ${keyRepository.createKeyFrom(masterKid)}")
    }
}