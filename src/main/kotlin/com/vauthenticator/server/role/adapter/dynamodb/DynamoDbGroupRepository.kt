package com.vauthenticator.server.role.adapter.dynamodb

import com.vauthenticator.server.role.domain.Group
import com.vauthenticator.server.role.domain.GroupRepository
import com.vauthenticator.server.role.domain.GroupWitRoles

class DynamoDbGroupRepository : GroupRepository {
    override fun loadFor(groupName: String): GroupWitRoles? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<Group> {
        TODO("Not yet implemented")
    }

    override fun save(group: Group) {
        TODO("Not yet implemented")
    }

    override fun delete(groupName: String) {
        TODO("Not yet implemented")
    }

    override fun roleAssociation(groupName: String, vararg roleNames: String) {
        TODO("Not yet implemented")
    }

    override fun roleDeAssociation(groupName: String, vararg roleNames: String) {
        TODO("Not yet implemented")
    }

}