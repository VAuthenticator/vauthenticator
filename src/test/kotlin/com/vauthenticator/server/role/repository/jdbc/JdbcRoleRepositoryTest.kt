package com.vauthenticator.server.role.repository.jdbc

import com.vauthenticator.server.role.RoleRepository
import com.vauthenticator.server.role.protectedRoleNames
import com.vauthenticator.server.role.repository.AbstractRoleRepositoryTest
import com.vauthenticator.server.support.JdbcUtils.initRoleTestsInDB
import com.vauthenticator.server.support.JdbcUtils.jdbcTemplate
import com.vauthenticator.server.support.JdbcUtils.resetDb

class JdbcRoleRepositoryTest : AbstractRoleRepositoryTest() {
    override fun initRoleRepository(): RoleRepository =
        JdbcRoleRepository(jdbcTemplate, protectedRoleNames)

    override fun resetDatabase() {
        resetDb()
        initRoleTestsInDB()
    }

}