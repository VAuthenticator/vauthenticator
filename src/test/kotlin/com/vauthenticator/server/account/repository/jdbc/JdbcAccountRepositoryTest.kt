package com.vauthenticator.server.account.repository.jdbc

import com.vauthenticator.server.account.repository.AbstractAccountRepositoryTest
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.role.RoleRepository
import com.vauthenticator.server.role.protectedRoleNames
import com.vauthenticator.server.role.repository.jdbc.JdbcRoleRepository
import com.vauthenticator.server.support.JdbcUtils.jdbcTemplate
import com.vauthenticator.server.support.JdbcUtils.resetDb

class JdbcAccountRepositoryTest : AbstractAccountRepositoryTest() {

    override fun initAccountRepository(roleRepository: RoleRepository): AccountRepository =
        JdbcAccountRepository(jdbcTemplate)

    override fun initRoleRepository(): RoleRepository = JdbcRoleRepository(jdbcTemplate, protectedRoleNames)

    override fun resetDatabase() {
        resetDb()
    }

}