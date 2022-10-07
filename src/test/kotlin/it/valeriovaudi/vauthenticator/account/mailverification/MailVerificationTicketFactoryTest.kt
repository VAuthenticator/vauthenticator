package it.valeriovaudi.vauthenticator.account.mailverification

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.account.tiket.TicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicketFeatures
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture.aClientApp
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.support.TicketFixture.ticketFor
import it.valeriovaudi.vauthenticator.time.Clocker
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Duration
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
internal class MailVerificationTicketFactoryTest {

    private val ticket = UUID.randomUUID().toString()
    private val ticketGenerator = { ticket }

    @MockK
    lateinit var clocker: Clocker

    @MockK
    private lateinit var ticketRepository: TicketRepository

    private lateinit var underTest: MailVerificationTicketFactory

    @BeforeEach
    internal fun setUp() {
        underTest = MailVerificationTicketFactory(ticketGenerator, ticketRepository, VerificationTicketFeatures(Duration.ofSeconds(100), false))
    }

    @Test
    internal fun `happy path`() {
        val now = Instant.ofEpochSecond(100)
        val account = anAccount()
        val clientApplication = aClientApp(ClientAppId("A_CLIENT_APP_ID"))

        val ticket = ticketFor(ticketGenerator.invoke(), "email@domain.com", "A_CLIENT_APP_ID")

        every { clocker.now() } returns now
        every { ticketRepository.store(ticket) } just runs


        val expected = VerificationTicket(ticketGenerator.invoke())
        val actual = underTest.createTicketFor(account, clientApplication)

        assertEquals(expected, actual)
    }

}