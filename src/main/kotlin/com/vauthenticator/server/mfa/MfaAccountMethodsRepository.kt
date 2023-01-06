package com.vauthenticator.server.mfa

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.valueAsStringFor
import com.vauthenticator.server.keys.*
import com.vauthenticator.server.mfa.MfaMethod.valueOf
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest

interface MfaAccountMethodsRepository {

    fun findAll(email: String): Map<MfaMethod, MfaAccountMethod>
    fun save(email: String, mfaMfaMethod: MfaMethod): MfaAccountMethod
}

class DynamoMfaAccountMethodsRepository(
    private val tableName: String,
    private val dynamoDbClient: DynamoDbClient,
    private val keyRepository: KeyRepository,
    private val masterKid: MasterKid
) : MfaAccountMethodsRepository {
    override fun findAll(email: String): Map<MfaMethod, MfaAccountMethod> {
        val fromDynamo = getFromDynamo(email)
        return fromDynamo.associate {
            val mfaMethod = valueOf(it.valueAsStringFor("mfa_method"))
            mfaMethod to MfaAccountMethod(
                email, Kid(it.valueAsStringFor("key_id")), mfaMethod
            )
        }
    }

    private fun getFromDynamo(email: String) =
        dynamoDbClient.query(
            QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("user_name=:email")
                .expressionAttributeValues(mapOf(":email" to email.asDynamoAttribute()))
                .build()
        ).items()

    override fun save(email: String, mfaMfaMethod: MfaMethod): MfaAccountMethod {
        val kid = keyRepository.createKeyFrom(masterKid, KeyType.SYMMETRIC, KeyPurpose.MFA)
        storeOnDynamo(email, mfaMfaMethod, kid)
        return MfaAccountMethod(email, kid, mfaMfaMethod)
    }

    private fun storeOnDynamo(
        email: String,
        mfaMfaMethod: MfaMethod,
        kid: Kid
    ) {
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(tableName)
                .item(
                    mapOf(
                        "user_name" to email.asDynamoAttribute(),
                        "mfa_method" to mfaMfaMethod.name.asDynamoAttribute(),
                        "key_id" to kid.content().asDynamoAttribute()
                    )
                )
                .build()
        )
    }

}