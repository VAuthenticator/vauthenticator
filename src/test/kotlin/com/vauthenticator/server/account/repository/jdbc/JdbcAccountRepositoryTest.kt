package com.vauthenticator.server.account.repository.jdbc

import com.vauthenticator.server.account.repository.AbstractAccountRepositoryTest
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.role.RoleRepository

class JdbcAccountRepositoryTest : AbstractAccountRepositoryTest() {

    override fun initAccountRepository(roleRepository: RoleRepository): AccountRepository = TODO()
    override fun initRoleRepository(): RoleRepository = TODO()
    override fun resetDatabase() = TODO()

}