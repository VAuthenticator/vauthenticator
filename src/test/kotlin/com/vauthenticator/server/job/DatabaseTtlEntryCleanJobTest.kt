package com.vauthenticator.server.job

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.vauthenticator.server.keys.adapter.jdbc.JdbcKeyStorage
import com.vauthenticator.server.keys.domain.DataKey
import com.vauthenticator.server.keys.domain.KeyPurpose.SIGNATURE
import com.vauthenticator.server.keys.domain.KeyType
import com.vauthenticator.server.keys.domain.Kid
import com.vauthenticator.server.keys.domain.MasterKid
import com.vauthenticator.server.support.AccountTestFixture
import com.vauthenticator.server.support.ClientAppFixture
import com.vauthenticator.server.support.JdbcUtils.jdbcTemplate
import com.vauthenticator.server.support.JdbcUtils.resetDb
import com.vauthenticator.server.support.TicketFixture
import com.vauthenticator.server.ticket.adapter.jdbc.JdbcTicketRepository
import com.vauthenticator.server.ticket.domain.TicketId
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock
import java.time.Duration
import java.util.*

@ExtendWith(MockKExtension::class)
class DatabaseTtlEntryCleanJobTest {

    @MockK
    lateinit var lockService: LockService

    @BeforeEach
    fun setUp() {
        resetDb()
    }

    @Test
    fun `when the old entries are deleted`() {
        val ticketRepository = JdbcTicketRepository(jdbcTemplate, jacksonObjectMapper())
        val keyStorage = JdbcKeyStorage(jdbcTemplate, Clock.systemDefaultZone())

        val uut = DatabaseTtlEntryCleanJob(jdbcTemplate, 100, lockService, Clock.systemUTC())

        val kid = Kid("")
        val anAccount = AccountTestFixture.anAccount()
        val aClientAppId = ClientAppFixture.aClientAppId()

        ticketRepository.store(TicketFixture.ticketFor("A_TICKET", anAccount.email, aClientAppId.content))
        keyStorage.store(
            MasterKid(""),
            kid,
            DataKey(ByteArray(0), Optional.empty()),
            KeyType.ASYMMETRIC,
            SIGNATURE
        )
        keyStorage.keyDeleteJodPlannedFor(kid, Duration.ofSeconds(-200), SIGNATURE)

        every {
            lockService.lock(100)
            lockService.unlock()
        } just runs

        uut.execute()

        verify {
            lockService.lock(100)
            lockService.unlock()
        }

        val actualTicket = ticketRepository.loadFor(TicketId("A_TICKET"))
        assertTrue(actualTicket.isEmpty)
        assertThrows(NoSuchElementException::class.java) {
            keyStorage.findOne(kid, SIGNATURE)
        }
    }
}