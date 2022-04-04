package it.valeriovaudi.vauthenticator.role

import it.valeriovaudi.vauthenticator.role.DynamoDbRoleMapper.deleteRoleRequestFor
import it.valeriovaudi.vauthenticator.role.DynamoDbRoleMapper.findAllRequestFor
import it.valeriovaudi.vauthenticator.role.DynamoDbRoleMapper.roleFor
import it.valeriovaudi.vauthenticator.role.DynamoDbRoleMapper.saveRoleRequestFor
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest

interface RoleRepository {

    fun findAll(): List<Role>
    fun save(role: Role)
    fun delete(role: String)

}

class DynamoDbRoleRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val tableName: String
) : RoleRepository {
    override fun findAll() =
        dynamoDbClient.scan(findAllRequestFor(tableName))
            .items()
            .map(::roleFor)

    override fun save(role: Role) =
        dynamoDbClient.putItem(saveRoleRequestFor(role, tableName))
            .let { }

    override fun delete(roleName: String) =
        dynamoDbClient.deleteItem(deleteRoleRequestFor(roleName, tableName))
            .let { }

}

object DynamoDbRoleMapper {
    fun findAllRequestFor(tableName: String): ScanRequest =
        ScanRequest.builder()
            .tableName(tableName)
            .build()

    fun saveRoleRequestFor(role: Role, tableName: String): PutItemRequest = PutItemRequest.builder()
        .tableName(tableName)
        .item(
            mutableMapOf(
                "role_name" to AttributeValue.builder().s(role.name).build(),
                "description" to AttributeValue.builder().s(role.description).build()
            )
        )
        .build()


    fun deleteRoleRequestFor(roleName: String, tableName: String): DeleteItemRequest = DeleteItemRequest.builder()
        .tableName(tableName)
        .key(
            mutableMapOf(
                "role_name" to AttributeValue.builder().s(roleName).build()
            )
        )
        .build()

    fun roleFor(it: MutableMap<String, AttributeValue>) =
        Role(
            it["role_name"]?.s()!!,
            it["description"]?.s().orEmpty()
        )

}