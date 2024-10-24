package com.vauthenticator.server.support

import org.postgresql.Driver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.SimpleDriverDataSource
import java.nio.file.Files
import java.nio.file.Paths

val logger: Logger = LoggerFactory.getLogger(JdbcUtils::class.java)

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
            jdbcTemplate.execute("DROP TABLE IF EXISTS CLIENT_APPLICATION;")
            jdbcTemplate.execute("DROP TABLE IF EXISTS ROLE CASCADE;")
            jdbcTemplate.execute("DROP TABLE IF EXISTS ACCOUNT CASCADE;")
            jdbcTemplate.execute("DROP TABLE IF EXISTS ACCOUNT_ROLE;")
            jdbcTemplate.execute("DROP TABLE IF EXISTS KEYS;")
            jdbcTemplate.execute("DROP TABLE IF EXISTS oauth2_authorization;")
            jdbcTemplate.execute(Files.readString(Paths.get("src/main/resources/data/schema.sql")))
        } catch (e: java.lang.Exception) {
            logger.error(e.message)
        }
    }

    fun initRoleTestsInDB() {
        jdbcTemplate.update("INSERT INTO ROLE (name,description) VALUES ('a_role','A_ROLE')")
    }
}