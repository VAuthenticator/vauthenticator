package it.valeriovaudi.vauthenticator.mfa

import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import it.valeriovaudi.vauthenticator.keys.KeyRepository
import it.valeriovaudi.vauthenticator.keys.KeyType
import it.valeriovaudi.vauthenticator.keys.Kid
import it.valeriovaudi.vauthenticator.keys.MasterKid
import it.valeriovaudi.vauthenticator.mfa.MfaMethod.valueOf
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest

interface MfaAccountMethodsRepository {

    fun findAll(email: String): Map<MfaMethod, MfaAccountMethod>
    fun save(email: String, mfaMfaMethod: MfaMethod): MfaAccountMethod
    fun delete(email: String): Nothing = TODO()
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
                email, Kid(it.valueAsStringFor("kid")), mfaMethod
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
        val kid = keyRepository.createKeyFrom(masterKid, KeyType.SYMMETRIC)
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
                        "kid" to kid.content().asDynamoAttribute()
                    )
                )
                .build()
        )
    }

}