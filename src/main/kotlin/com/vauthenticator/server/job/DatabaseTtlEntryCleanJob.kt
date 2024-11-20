package com.vauthenticator.server.job

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForList
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional
import java.time.Clock


@Transactional
class DatabaseTtlEntryCleanJob(
    private val jdbcTemplate: JdbcTemplate,
    private val clock: Clock
) {

    @Scheduled(cron = "\${scheduled.database-cleanup.cron}")
    fun execute() {
        val now = clock.instant().epochSecond

        deleteOldTicket(now)
        deleteOldKeys(now)
    }

    private fun deleteOldKeys(now: Long) {
        val keysToBeDeleted =
            jdbcTemplate.queryForList("SELECT key_id,key_purpose  FROM KEYS WHERE key_expiration_date_timestamp < ?", now)
        keysToBeDeleted.forEach {
            jdbcTemplate.update(
                "DELETE FROM KEYS WHERE  key_id = ? AND key_purpose = ?;", it["key_id"], it["key_purpose"]
            )
        }
    }

    private fun deleteOldTicket(now: Long) {
        val ticketToBeDeleted =
            jdbcTemplate.queryForList<String>(
                "SELECT ticket FROM TICKET WHERE ttl < ?",
                arrayOf(now)
            )
        ticketToBeDeleted.forEach {
            jdbcTemplate.update("DELETE FROM TICKET WHERE ticket = ?", it)
        }
    }

}