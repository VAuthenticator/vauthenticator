package com.vauthenticator.account.mailverification

import com.vauthenticator.account.AccountTestFixture.anAccount
import com.vauthenticator.account.tiket.TicketRepository
import com.vauthenticator.account.tiket.VerificationTicket
import com.vauthenticator.account.tiket.VerificationTicketFactory
import com.vauthenticator.account.tiket.VerificationTicketFeatures
import com.vauthenticator.oauth2.clientapp.ClientAppId
import com.vauthenticator.support.TicketFixture.ticketFor
import com.vauthenticator.time.Clocker
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.*
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

        val ticket = ticketFor(ticketGenerator.invoke(), "email@domain.com", "A_CLIENT_APP_ID")

        every { clocker.now() } returns now
        every { ticketRepository.store(ticket) } just runs


        val expected = VerificationTicket(ticketGenerator.invoke())
        val actual = underTest.createTicketFor(account, ClientAppId("A_CLIENT_APP_ID"))

        assertEquals(expected, actual)
    }

}