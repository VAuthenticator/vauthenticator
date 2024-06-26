package com.vauthenticator.server.support

import org.postgresql.Driver
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SimpleDriverDataSource
import java.nio.file.Files
import java.nio.file.Paths


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
            jdbcTemplate.execute("DROP TABLE IF EXISTS ROLE CASCADE;")
            jdbcTemplate.execute("DROP TABLE IF EXISTS ACCOUNT CASCADE;")
            jdbcTemplate.execute("DROP TABLE IF EXISTS ACCOUNT_ROLE;")
            jdbcTemplate.execute(Files.readString(Paths.get("src/main/resources/data/schema.sql")))
        } catch (e: java.lang.Exception) {
            println(e)
        }
    }

    fun initRoleTestsInDB() {
        jdbcTemplate.update("INSERT INTO ROLE (name,description) VALUES ('a_role','A_ROLE')")
    }
}