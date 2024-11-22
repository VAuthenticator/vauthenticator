package com.vauthenticator.server.job

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForList
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Clock

@Transactional
class DatabaseTtlEntryCleanJob(
    private val jdbcTemplate: JdbcTemplate,
    private val lockTtl: Long,
    private val lockService: LockService,
    private val clock: Clock
) {

    private val logger = LoggerFactory.getLogger(DatabaseTtlEntryCleanJob::class.java)

    @Scheduled(cron = "\${scheduled.database-cleanup.cron}")
    fun execute() {
        try {
            logger.info("Try to Schedule")

            lockService.lock(lockTtl)
            logger.info("Job Running")
            val now = clock.instant().epochSecond

            deleteOldTicket(now)
            deleteOldKeys(now)
            logger.info("Job Completed")
        } finally {
            lockService.unlock()
        }

    }

    private fun deleteOldKeys(now: Long) {
        val keysToBeDeleted =
            jdbcTemplate.queryForList(
                "SELECT key_id,key_purpose  FROM KEYS WHERE key_expiration_date_timestamp < ?",
                now
            )
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


@Profile("!kms")
@EnableScheduling
@Configuration(proxyBeanMethods = false)
class DatabaseTtlEntryCleanJobConfig {

    @Bean
    fun databaseTtlEntryCleanJob(
        @Value("\${scheduled.database-cleanup.lock-ttl}") lockTtl: Long,
        lockService: LockService,
        jdbcTemplate: JdbcTemplate
    ) = DatabaseTtlEntryCleanJob(jdbcTemplate, lockTtl, lockService, Clock.systemUTC())

    @Bean
    fun lockService(redisTemplate: RedisTemplate<String, String>) = RedisLockService(redisTemplate)
}