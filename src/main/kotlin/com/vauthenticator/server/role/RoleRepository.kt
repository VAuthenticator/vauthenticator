package com.vauthenticator.server.role

import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.valueAsStringFor
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest

interface RoleRepository {

    fun findAll(): List<Role>
    fun save(role: Role)
    fun delete(role: String)

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
                "role_name" to role.name.asDynamoAttribute(),
                "description" to role.description.asDynamoAttribute()
            )
        )
        .build()


    fun deleteRoleRequestFor(roleName: String, tableName: String): DeleteItemRequest = DeleteItemRequest.builder()
        .tableName(tableName)
        .key(
            mutableMapOf(
                "role_name" to roleName.asDynamoAttribute()
            )
        )
        .build()

    fun roleFor(it: MutableMap<String, AttributeValue>) =
        Role(
            it.valueAsStringFor("role_name"),
            it.valueAsStringFor("description", "")
        )

}