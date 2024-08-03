package com.vauthenticator.server.mfa.adapter.dynamodb

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.valueAsBoolFor
import com.vauthenticator.server.extentions.valueAsStringFor
import com.vauthenticator.server.keys.*
import com.vauthenticator.server.mfa.domain.MfaAccountMethod
import com.vauthenticator.server.mfa.domain.MfaAccountMethodsRepository
import com.vauthenticator.server.mfa.domain.MfaDeviceId
import com.vauthenticator.server.mfa.domain.MfaMethod
import com.vauthenticator.server.mfa.domain.MfaMethod.valueOf
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.util.*

class DynamoMfaAccountMethodsRepository(
    private val tableName: String,
    private val dynamoDbClient: DynamoDbClient,
    private val keyRepository: KeyRepository,
    private val masterKid: MasterKid,
    private val mfaDeviceIdGenerator: () -> MfaDeviceId
) : MfaAccountMethodsRepository {

    override fun findBy(
        userName: String,
        mfaMfaMethod: MfaMethod,
        mfaChannel: String
    ): Optional<MfaAccountMethod> {
        return Optional.ofNullable(
            getFromDynamo(userName, mfaChannel)
                .map { MfaAccountMethodMapper.fromDynamoToDomain(userName, it) }
                .find { it.method == mfaMfaMethod }
        )
    }

    override fun findBy(mfaDeviceId: MfaDeviceId): Optional<MfaAccountMethod> {
        TODO("Not yet implemented")
    }


    override fun findAll(userName: String): List<MfaAccountMethod> =
        getFromDynamo(userName)
            .map { MfaAccountMethodMapper.fromDynamoToDomain(userName, it) }

    private fun getFromDynamo(email: String) = dynamoDbClient.query(
        QueryRequest.builder().tableName(tableName).keyConditionExpression("user_name=:email")
            .expressionAttributeValues(mapOf(":email" to email.asDynamoAttribute())).build()
    ).items()

    private fun getFromDynamo(email: String, mfaChannel: String) = dynamoDbClient.query(
        QueryRequest.builder().tableName(tableName)
            .keyConditionExpression("user_name=:email AND mfa_channel=:mfaChannel")
            .expressionAttributeValues(
                mapOf(
                    ":email" to email.asDynamoAttribute(),
                    ":mfaChannel" to mfaChannel.asDynamoAttribute(),
                )
            )
            .build()
    ).items()

    override fun save(
        userName: String,
        mfaMfaMethod: MfaMethod,
        mfaChannel: String,
        associated: Boolean
    ): MfaAccountMethod {
        val kid = keyRepository.createKeyFrom(masterKid, KeyType.SYMMETRIC, KeyPurpose.MFA)
        val mfaDeviceId = mfaDeviceIdGenerator.invoke()
        storeOnDynamo(userName, mfaMfaMethod, mfaChannel,mfaDeviceId, kid, associated)
        return MfaAccountMethod(userName, mfaDeviceId, kid, mfaMfaMethod, mfaChannel, associated)
    }

    override fun setAsDefault(userName: String, mfaDeviceId: MfaDeviceId) {
        TODO("Not yet implemented")
    }

    private fun storeOnDynamo(
        userName: String, mfaMfaMethod: MfaMethod, mfaChannel: String, mfaDeviceId : MfaDeviceId, kid: Kid, associated: Boolean
    ) {
        dynamoDbClient.putItem(
            PutItemRequest.builder().tableName(tableName).item(
                mapOf(
                    "user_name" to userName.asDynamoAttribute(),
                    "user_name" to userName.asDynamoAttribute(),
                    "mfa_method" to mfaMfaMethod.name.asDynamoAttribute(),
                    "mfa_channel" to mfaChannel.asDynamoAttribute(),
                    "mfa_device_id" to mfaDeviceId.content.asDynamoAttribute(),
                    "key_id" to kid.content().asDynamoAttribute(),
                    "associated" to associated.asDynamoAttribute()
                )
            ).build()
        )
    }

}


object MfaAccountMethodMapper {
    fun fromDynamoToDomain(
        userName: String,
        item: MutableMap<String, AttributeValue>
    ): MfaAccountMethod =
        MfaAccountMethod(
            userName,
            MfaDeviceId(item.valueAsStringFor("mfa_device_id")),
            Kid(item.valueAsStringFor("key_id")),
            valueOf(item.valueAsStringFor("mfa_method")),
            item.valueAsStringFor("mfa_channel"),
            item.valueAsBoolFor("associated")
        )
}