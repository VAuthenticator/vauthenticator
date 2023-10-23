package com.vauthenticator.server.account.mailverification

import com.vauthenticator.server.account.AccountTestFixture.anAccount
import com.vauthenticator.server.account.EMAIL
import com.vauthenticator.server.account.ticket.TicketRepository
import com.vauthenticator.server.account.ticket.VerificationTicket
import com.vauthenticator.server.account.ticket.VerificationTicketFactory
import com.vauthenticator.server.account.ticket.VerificationTicketFeatures
import com.vauthenticator.server.clientapp.A_CLIENT_APP_ID
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.support.TicketFixture.ticketFor
import com.vauthenticator.server.time.Clocker
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
internal class VerificationTicketFactoryTest {

    private val ticket = UUID.randomUUID().toString()
    private val ticketGenerator = { ticket }

    @MockK
    lateinit var clocker: Clocker

    @MockK
    private lateinit var ticketRepository: TicketRepository


    private lateinit var underTest: VerificationTicketFactory

    @BeforeEach
    internal fun setUp() {
        underTest = VerificationTicketFactory(ticketGenerator, clocker, ticketRepository, VerificationTicketFeatures(Duration.ofSeconds(100)))
    }

    @Test
    internal fun `happy path`() {
        val now = Instant.ofEpochSecond(100)
        val account = anAccount()

        val ticket = ticketFor(ticketGenerator.invoke(), EMAIL, A_CLIENT_APP_ID)

        every { clocker.now() } returns now
        every { ticketRepository.store(ticket) } just runs


        val expected = VerificationTicket(ticketGenerator.invoke())
        val actual = underTest.createTicketFor(account, ClientAppId(A_CLIENT_APP_ID))

        assertEquals(expected, actual)
    }

}