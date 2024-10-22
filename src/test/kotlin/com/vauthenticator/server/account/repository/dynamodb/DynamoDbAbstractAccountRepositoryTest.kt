package com.vauthenticator.server.account.repository.dynamodb

import com.vauthenticator.server.account.repository.AbstractAccountRepositoryTest
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.role.domain.RoleRepository
import com.vauthenticator.server.support.protectedRoleNames
import com.vauthenticator.server.role.adapter.dynamodb.DynamoDbRoleRepository
import com.vauthenticator.server.support.DynamoDbUtils.dynamoAccountTableName
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDbClient
import com.vauthenticator.server.support.DynamoDbUtils.dynamoRoleTableName
import com.vauthenticator.server.support.DynamoDbUtils.resetDynamoDb


internal class DynamoDbAbstractAccountRepositoryTest : AbstractAccountRepositoryTest() {


    override fun initUnitUnderTest(roleRepository: RoleRepository): AccountRepository =
        DynamoDbAccountRepository(
            dynamoDbClient,
            dynamoAccountTableName,
            roleRepository
        )

    override fun initRoleRepository(): RoleRepository =
        DynamoDbRoleRepository(
            protectedRoleNames,
            dynamoDbClient,
            dynamoRoleTableName
        )


    override fun resetDatabase() {
        resetDynamoDb()
    }

}