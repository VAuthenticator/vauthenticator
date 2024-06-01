package com.vauthenticator.server.account.repository.dynamodb

import com.vauthenticator.server.account.repository.AbstractAccountRepositoryTest
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.role.DynamoDbRoleRepository
import com.vauthenticator.server.role.RoleRepository
import com.vauthenticator.server.role.protectedRoleNames
import com.vauthenticator.server.support.DynamoDbUtils.dynamoAccountTableName
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDbClient
import com.vauthenticator.server.support.DynamoDbUtils.dynamoRoleTableName
import com.vauthenticator.server.support.DynamoDbUtils.resetDynamoDb
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File


@Testcontainers
internal class DynamoDbAbstractAccountRepositoryTest : AbstractAccountRepositoryTest() {

    companion object {
        @Container
        var localStack: ComposeContainer =
            ComposeContainer(File("src/test/resources/docker-compose.yml"))
                .withExposedService("localstack", 4566, Wait.forListeningPort())
    }

    override fun initAccountRepository(roleRepository: RoleRepository): AccountRepository =
        DynamoDbAccountRepository(
            dynamoDbClient(localStack),
            dynamoAccountTableName,
            roleRepository
        )

    override fun initRoleRepository(): RoleRepository =
        DynamoDbRoleRepository(
            protectedRoleNames,
            dynamoDbClient(localStack),
            dynamoRoleTableName
        )


    override fun resetDatabase() {
        resetDynamoDb(dynamoDbClient(localStack))
    }

}