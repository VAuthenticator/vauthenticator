package com.vauthenticator.server.role.repository.dynamodb

import com.vauthenticator.server.role.RoleRepository
import com.vauthenticator.server.role.protectedRoleNames
import com.vauthenticator.server.role.repository.AbstractRoleRepositoryTest
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDbClient
import com.vauthenticator.server.support.DynamoDbUtils.dynamoRoleTableName
import com.vauthenticator.server.support.DynamoDbUtils.initRoleTests
import com.vauthenticator.server.support.DynamoDbUtils.resetDynamoDb

class DynamoDbRoleRepositoryTest : AbstractRoleRepositoryTest() {
    override fun initRoleRepository(): RoleRepository =
        DynamoDbRoleRepository(protectedRoleNames, dynamoDbClient, dynamoRoleTableName)

    override fun resetDatabase() {
        resetDynamoDb()
        initRoleTests()
    }

}