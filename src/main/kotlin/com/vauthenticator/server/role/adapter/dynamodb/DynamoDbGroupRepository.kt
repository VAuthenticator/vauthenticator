package com.vauthenticator.server.role.adapter.dynamodb

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.extentions.asDynamoAttribute
import com.vauthenticator.server.extentions.filterEmptyMetadata
import com.vauthenticator.server.extentions.valueAsLongFor
import com.vauthenticator.server.extentions.valueAsStringFor
import com.vauthenticator.server.role.domain.*
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

private const val GROUP_NAME_FIELD_NAME = "group_name"
private const val DESCRIPTION_FIELD_NAME = "description"
private const val ROLES_FIELD_NAME = "roles"
private const val VERSION_FIELD_NAME = "version"

class DynamoDbGroupRepository(
    private val objectMapper: ObjectMapper,
    private val groupTableName: String,
    private val groupToRoleAssociationTableName: String,
    private val dynamoDbClient: DynamoDbClient,
    private val roleRepository: RoleRepository
) : GroupRepository {
    override fun loadFor(groupName: String): GroupWitRoles? {
        return Optional.ofNullable(
            dynamoDbClient.getItem {
                it.tableName(groupTableName)
                    .key(
                        mutableMapOf(
                            GROUP_NAME_FIELD_NAME to groupName.asDynamoAttribute()
                        )
                    )
            }.item()
        ).flatMap { it.filterEmptyMetadata() }
            .map {
                val roles = dynamoDbClient.getItem {
                    it.tableName(groupToRoleAssociationTableName)
                        .key(
                            mutableMapOf(
                                GROUP_NAME_FIELD_NAME to groupName.asDynamoAttribute()
                            )
                        )
                }.item()
                    .let {
                        rolesFrom(it)
                    }

                GroupWitRoles(
                    group = Group(
                        name = it.valueAsStringFor(GROUP_NAME_FIELD_NAME),
                        description = it.valueAsStringFor(DESCRIPTION_FIELD_NAME)
                    ),
                    roles = roles
                )
            }.getOrNull()

    }

    override fun findAll(): List<Group> {
        return dynamoDbClient.scan {
            it.tableName(groupTableName)
        }.items()
            .map {
                Group(
                    it.valueAsStringFor(GROUP_NAME_FIELD_NAME),
                    it.valueAsStringFor(DESCRIPTION_FIELD_NAME)
                )
            }
    }

    override fun save(group: Group) {
        dynamoDbClient.putItem {
            it.item(
                mutableMapOf(
                    GROUP_NAME_FIELD_NAME to group.name.asDynamoAttribute(),
                    DESCRIPTION_FIELD_NAME to group.description.asDynamoAttribute()
                )
            ).tableName(groupTableName)
        }
    }

    override fun delete(groupName: String) {
        dynamoDbClient.deleteItem {
            it.tableName(groupToRoleAssociationTableName)
                .key(
                    mutableMapOf(
                        GROUP_NAME_FIELD_NAME to groupName.asDynamoAttribute()
                    )
                )
        }

        dynamoDbClient.deleteItem {
            it.tableName(groupTableName)
                .key(
                    mutableMapOf(
                        GROUP_NAME_FIELD_NAME to groupName.asDynamoAttribute()
                    )
                )
        }
    }

    override fun roleAssociation(groupName: String, vararg roleNames: String) {
        var version = 0L
        val allRoles = roleRepository.findAll()
        val roles = dynamoDbClient.getItem {
            it.tableName(groupToRoleAssociationTableName)
                .key(
                    mutableMapOf(
                        GROUP_NAME_FIELD_NAME to groupName.asDynamoAttribute()
                    )
                )
        }
            .item()
            .let {
                version = it.valueAsLongFor(VERSION_FIELD_NAME, 0)
                rolesFrom(it) +
                        arrayOf(*roleNames).map {
                            Role(
                                it,
                                allRoles.find { role -> role.name == it }?.description ?: ""
                            )
                        }
            }.toSet()

        version += 1
        dynamoDbClient.putItem {
            it.tableName(groupToRoleAssociationTableName)
//                .conditionExpression("$VERSION_FIELD_NAME=:version")
//                .expressionAttributeValues(mutableMapOf(":version" to (version).asDynamoAttribute()))
                .item(
                    mutableMapOf(
                        GROUP_NAME_FIELD_NAME to groupName.asDynamoAttribute(),
                        ROLES_FIELD_NAME to roles.map { objectMapper.writeValueAsString(it) }.asDynamoAttribute(),
                        VERSION_FIELD_NAME to (version).asDynamoAttribute()
                    )
                )
        }
    }

    override fun roleDeAssociation(groupName: String, vararg roleNames: String) {
        var version = 0L
        val allRoles = roleRepository.findAll()

        val roles = dynamoDbClient.getItem {
            it.tableName(groupToRoleAssociationTableName)
                .key(
                    mutableMapOf(
                        GROUP_NAME_FIELD_NAME to groupName.asDynamoAttribute()
                    )
                )
        }
            .item()
            .let {
                version = it.valueAsLongFor(VERSION_FIELD_NAME, 0)
                rolesFrom(it) -
                        arrayOf(*roleNames).map {
                            Role(
                                it,
                                allRoles.find { role -> role.name == it }?.description ?: ""
                            )
                        }
            }


        version += 1
        dynamoDbClient.putItem {
            it
                //            .conditionExpression("version < :version")
//                .expressionAttributeValues(mutableMapOf(":version" to (version).asDynamoAttribute()))
                .tableName(groupToRoleAssociationTableName)
                .item(
                    mutableMapOf(
                        GROUP_NAME_FIELD_NAME to groupName.asDynamoAttribute(),
                        ROLES_FIELD_NAME to roles.map { objectMapper.writeValueAsString(it) }.asDynamoAttribute(),
                        VERSION_FIELD_NAME to (version).asDynamoAttribute()
                    )
                )
        }
    }

    private fun rolesFrom(map: MutableMap<String, AttributeValue>): List<Role> {
        return if (map.isNotEmpty()) {
            map.getValue(ROLES_FIELD_NAME).ss().map { objectMapper.readValue(it, Role::class.java) }

        } else {
            emptyList()
        }
    }

}