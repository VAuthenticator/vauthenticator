package com.vauthenticator.server.mfa.repository

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.valueAsStringFor
import com.vauthenticator.server.keys.*
import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.domain.MfaMethod.valueOf
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.util.*

class DynamoMfaAccountMethodsRepository(
    private val tableName: String,
    private val dynamoDbClient: DynamoDbClient,
    private val keyRepository: KeyRepository,
    private val masterKid: MasterKid
) : MfaAccountMethodsRepository {

    override fun findOne(email: String, mfaMfaMethod: MfaMethod): Optional<MfaAccountMethod> =
        Optional.ofNullable(findAll(email).find { it.method == MfaMethod.EMAIL_MFA_METHOD })


    override fun findAll(email: String): List<MfaAccountMethod> =
        getFromDynamo(email).map {
            MfaAccountMethod(
                email, Kid(it.valueAsStringFor("key_id")), valueOf(it.valueAsStringFor("mfa_method"))
            )
        }

    private fun getFromDynamo(email: String) = dynamoDbClient.query(
        QueryRequest.builder().tableName(tableName).keyConditionExpression("user_name=:email")
            .expressionAttributeValues(mapOf(":email" to email.asDynamoAttribute())).build()
    ).items()

    override fun save(email: String, mfaMfaMethod: MfaMethod): MfaAccountMethod {
        val kid = keyRepository.createKeyFrom(masterKid, KeyType.SYMMETRIC, KeyPurpose.MFA)
        storeOnDynamo(email, mfaMfaMethod, kid)
        return MfaAccountMethod(email, kid, mfaMfaMethod)
    }

    private fun storeOnDynamo(
        email: String, mfaMfaMethod: MfaMethod, kid: Kid
    ) {
        dynamoDbClient.putItem(
            PutItemRequest.builder().tableName(tableName).item(
                mapOf(
                    "user_name" to email.asDynamoAttribute(),
                    "mfa_method" to mfaMfaMethod.name.asDynamoAttribute(),
                    "key_id" to kid.content().asDynamoAttribute()
                )
            ).build()
        )
    }

}