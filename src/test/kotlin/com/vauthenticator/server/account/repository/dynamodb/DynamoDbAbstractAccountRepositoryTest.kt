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
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName


@Testcontainers
internal class DynamoDbAbstractAccountRepositoryTest : AbstractAccountRepositoryTest() {

    companion object {
        @Container
        @ServiceConnection("localstack")
        var localStack =
            GenericContainer(DockerImageName.parse("localstack/localstack:3.2"))
                .withExposedPorts(4566)
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
        resetDynamoDb(dynamoDbClient(localStack.getMappedPort(4566)))
    }

}