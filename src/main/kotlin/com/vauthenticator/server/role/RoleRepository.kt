package com.vauthenticator.server.role

import com.vauthenticator.server.cache.CacheContentConverter
import com.vauthenticator.server.cache.CacheOperation
import com.vauthenticator.server.role.DynamoDbRoleMapper.deleteRoleRequestFor
import com.vauthenticator.server.role.DynamoDbRoleMapper.findAllRequestFor
import com.vauthenticator.server.role.DynamoDbRoleMapper.roleFor
import com.vauthenticator.server.role.DynamoDbRoleMapper.saveRoleRequestFor
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

private const val ROLES_CACHE_KEY = "roles"

class CachedRoleRepository(
    private val cacheContentConverter: CacheContentConverter<List<Role>>,
    private val cacheOperation: CacheOperation<String, String>,
    private val delegate: RoleRepository
) : RoleRepository by delegate {

    override fun findAll(): List<Role> {
        return cacheOperation.get(ROLES_CACHE_KEY)
            .map { cacheContentConverter.getObjectFromCacheContentFor(it) }
            .orElseGet {
                val loadedRoles = delegate.findAll()
                cacheOperation.put(
                    ROLES_CACHE_KEY,
                    cacheContentConverter.loadableContentIntoCacheFor(loadedRoles)
                )
                loadedRoles
            }
    }

    override fun save(role: Role) {
        cacheOperation.evict(ROLES_CACHE_KEY)
        delegate.save(role)
    }

    override fun delete(role: String) {
        cacheOperation.evict(ROLES_CACHE_KEY)
        delegate.delete(role)
    }
}

class DynamoDbRoleRepository(
    private val protectedRoleFromDeletion : List<String>,
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
        if (protectedRoleFromDeletion.contains(roleName)) {
            throw DefaultRoleDeleteException("the role $roleName is not allowed to be deleted")
        } else {
            dynamoDbClient.deleteItem(deleteRoleRequestFor(roleName, tableName))
                .let { }
        }


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