package com.vauthenticator.server.account.repository.dynamodb

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.repository.AccountRepositoryTest
import com.vauthenticator.server.role.DynamoDbRoleRepository
import com.vauthenticator.server.role.RoleRepository
import com.vauthenticator.server.role.protectedRoleNames
import com.vauthenticator.server.support.DynamoDbUtils.dynamoAccountTableName
import com.vauthenticator.server.support.DynamoDbUtils.dynamoDbClient
import com.vauthenticator.server.support.DynamoDbUtils.dynamoRoleTableName
import com.vauthenticator.server.support.DynamoDbUtils.resetDynamoDb
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@Testcontainers
internal class DynamoDbAccountRepositoryTest : AccountRepositoryTest() {

    companion object {
        @Container
        var localStack: LocalStackContainer =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:3.2"))
                .withServices(LocalStackContainer.Service.DYNAMODB)

    }

    override fun initAccountRepository(roleRepository: RoleRepository): AccountRepository =
        DynamoDbAccountRepository(
            dynamoDbClient(localStack.getMappedPort(4566)),
            dynamoAccountTableName,
            roleRepository
        )

    override fun initRoleRepository(): RoleRepository =
        DynamoDbRoleRepository(
            protectedRoleNames,
            dynamoDbClient(localStack.getMappedPort(4566)),
            dynamoRoleTableName
        )


    override fun resetDatabase() {
        val port = localStack.getMappedPort(4566)
        println("port: $port")
        resetDynamoDb(dynamoDbClient(port))
    }

}