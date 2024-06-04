package com.vauthenticator.server.support

import org.postgresql.Driver
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SimpleDriverDataSource


object JdbcUtils {

    val jdbcTemplate: JdbcTemplate = JdbcTemplate(
        SimpleDriverDataSource(
            Driver(),
            "jdbc:postgresql://localhost:5432/",
            "postgres",
            "postgres"
        )
    )


    fun resetDb() {
        try {
            jdbcTemplate.execute("TRUNCATE TABLE ACCOUNT CASCADE;")
            jdbcTemplate.execute("TRUNCATE TABLE ROLE CASCADE;")
        } catch (e: java.lang.Exception) {
            println(e)
        }
    }

    fun initRoleTestsInDB() {
        jdbcTemplate.update("INSERT INTO ROLE (name,description) VALUES ('a_role','A_ROLE')")
    }
}