package it.valeriovaudi.vauthenticator.support

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import it.valeriovaudi.vauthenticator.extentions.asDynamoAttribute
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest
import java.net.URI

object TestingFixture {

    const val dynamoRoleTableName: String = "TESTING_VAuthenticator_Role"
    const val dynamoAccountTableName: String = "TESTING_VAuthenticator_Account"
    const val dynamoAccountRoleTableName: String = "TESTING_VAuthenticator_Account_Role"
    const val dynamoClientApplicationTableName: String = "TESTING_VAuthenticator_ClientApplication"
    val key: ECKey = ECKeyGenerator(Curve.P_256)
            .keyID("123")
            .generate()

    fun simpleJwtFor(clientAppId: String, email: String = ""): String {
        val header = JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(JOSEObjectType.JWT)
                .keyID("123")
                .build();


        var claim = JWTClaimsSet.Builder()
                .claim(IdTokenClaimNames.AZP, clientAppId)

        if(email.isNotBlank()){
            claim = claim.claim("user_name", email)
        }

        val payload = claim.build()

        val signedJWT = SignedJWT(header, payload)
        signedJWT.sign(ECDSASigner(key.toECPrivateKey()))

        return signedJWT.serialize()
    }

    val dynamoDbClient: DynamoDbClient = DynamoDbClient.builder()
            .credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("ACCESS_KEY_ID", "SECRET_ACCESS_KEY"))
            ).region(Region.US_EAST_1)
            .endpointOverride(URI.create("http://localhost:8000"))
            .build()

    fun initRoleTests(roleRepository: DynamoDbClient) {
        val roleName = AttributeValue.builder().s("a_role").build()
        val description = AttributeValue.builder().s("A_ROLE").build()
        val item = PutItemRequest.builder()
                .tableName(dynamoRoleTableName)
                .item(
                        mutableMapOf(
                                "role_name" to roleName,
                                "description" to description
                        )
                )
                .build()
        roleRepository.putItem(item)
    }

    fun resetDatabase(client: DynamoDbClient) {
        scanFor(client, dynamoRoleTableName, "role_name")
                .forEach {
                    val deleteItemRequest = DeleteItemRequest.builder().tableName(dynamoRoleTableName)
                            .key(
                                    mutableMapOf(
                                            "role_name" to it.valueAsStringFor("role_name").asDynamoAttribute()
                                    )
                            )
                            .build()
                    client.deleteItem(deleteItemRequest)
                }

        scanFor(client, dynamoAccountTableName, "user_name")
                .forEach {
                    val deleteItemRequest = DeleteItemRequest.builder().tableName(dynamoAccountTableName)
                            .key(
                                    mutableMapOf(
                                            "user_name" to it.valueAsStringFor("user_name").asDynamoAttribute(),
                                    )
                            )
                            .build()
                    client.deleteItem(deleteItemRequest)
                }

        scanFor(client, dynamoAccountRoleTableName, "user_name", "role_name")
                .forEach {
                    val deleteItemRequest = DeleteItemRequest.builder().tableName(dynamoAccountRoleTableName)
                            .key(
                                    mutableMapOf(
                                            "user_name" to it.valueAsStringFor("user_name").asDynamoAttribute(),
                                            "role_name" to it.valueAsStringFor("role_name").asDynamoAttribute()
                                    )
                            )
                            .build()
                    client.deleteItem(deleteItemRequest)
                }

        scanFor(client, dynamoClientApplicationTableName, "client_id")
                .forEach {
                    val deleteItemRequest = DeleteItemRequest.builder().tableName(dynamoClientApplicationTableName)
                            .key(
                                    mutableMapOf(
                                            "client_id" to it.valueAsStringFor("client_id").asDynamoAttribute(),
                                    )
                            )
                            .build()
                    client.deleteItem(deleteItemRequest)
                }
    }

    private fun scanFor(
            client: DynamoDbClient,
            tableName: String,
            vararg attributeToGet: String
    ): MutableList<MutableMap<String, AttributeValue>> =
            try {
                client.scan(
                        ScanRequest.builder()
                                .tableName(tableName)
                                .attributesToGet(*attributeToGet)
                                .build()
                ).items()
            } catch (e: Exception) {
                println("e.message ${e.message}")
                mutableListOf()
            }

}