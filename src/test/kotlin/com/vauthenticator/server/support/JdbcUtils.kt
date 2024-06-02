package com.vauthenticator.server.support

import org.springframework.jdbc.core.JdbcTemplate


object JdbcUtils {

    val jdbcTemplate: JdbcTemplate = JdbcTemplate()


    fun resetDb() {
        try {
            jdbcTemplate
        } catch (e: java.lang.Exception) {
            println(e)
        }


    }
}