package com.vauthenticator.server.account.emailverification

import com.vauthenticator.server.clientapp.A_CLIENT_APP_ID
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import com.vauthenticator.server.support.EMAIL
import com.vauthenticator.server.support.TicketFixture.ticketFor
import com.vauthenticator.server.ticket.TicketRepository
import com.vauthenticator.server.ticket.VerificationTicket
import com.vauthenticator.server.ticket.VerificationTicketFactory
import com.vauthenticator.server.ticket.VerificationTicketFeatures
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.*

@ExtendWith(MockKExtension::class)
internal class VerificationTicketFactoryTest {

    private val ticket = UUID.randomUUID().toString()
    private val ticketGenerator = { ticket }

    @MockK
    private lateinit var ticketRepository: TicketRepository

    private lateinit var underTest: VerificationTicketFactory

    @BeforeEach
    internal fun setUp() {
        val clock = Clock.fixed(Instant.ofEpochSecond(100), ZoneId.systemDefault())
        underTest = VerificationTicketFactory(
            ticketGenerator,
            clock,
            ticketRepository,
            VerificationTicketFeatures(Duration.ofSeconds(100))
        )
    }

    @Test
    internal fun `happy path`() {
        val account = anAccount()

        val ticket = ticketFor(ticketGenerator.invoke(), EMAIL, A_CLIENT_APP_ID)

        every { ticketRepository.store(ticket) } just runs

        val expected = VerificationTicket(ticketGenerator.invoke())
        val actual = underTest.createTicketFor(account, ClientAppId(A_CLIENT_APP_ID))

        assertEquals(expected, actual)
    }

}