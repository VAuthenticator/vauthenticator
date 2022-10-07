package it.valeriovaudi.vauthenticator.account.mailverification

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import it.valeriovaudi.vauthenticator.account.AccountTestFixture
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.InsufficientTicketException
import it.valeriovaudi.vauthenticator.account.tiket.TicketRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.oauth2.clientapp.*
import it.valeriovaudi.vauthenticator.support.TicketFixture
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class VerifyMailChallengeSentTest {

    @MockK
    lateinit var clientAccountRepository: ClientApplicationRepository

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var ticketRepository: TicketRepository

    private lateinit var underTest: VerifyMailChallengeSent

    @BeforeEach
    fun setup() {
        underTest = VerifyMailChallengeSent(
                clientAccountRepository,
                accountRepository,
                ticketRepository
        )
    }

    @Test
    internal fun `happy path`() {
        val clientAppId = ClientAppId("A_CLIENT_APP_ID")
        val account = AccountTestFixture.anAccount()
        val clientApplication = ClientAppFixture.aClientApp(clientAppId).copy(scopes = Scopes.from(Scope.MAIL_VERIFY))
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(TicketFixture.ticketFor(verificationTicket.content, account.email, clientAppId.content))
        every { clientAccountRepository.findOne(clientAppId) } returns Optional.of(clientApplication)
        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { accountRepository.save(account.copy(accountNonLocked = true, enabled = true, emailVerified = true)) } just runs
        every { ticketRepository.delete(verificationTicket) } just runs

        underTest.verifyMail("A_TICKET")
    }

    @Test
    internal fun `when client application does not support mail verification`() {
        val clientAppId = ClientAppId("A_CLIENT_APP_ID")
        val account = AccountTestFixture.anAccount()
        val clientApplication = ClientAppFixture.aClientApp(clientAppId)
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(TicketFixture.ticketFor(verificationTicket.content, account.email, clientAppId.content))
        every { clientAccountRepository.findOne(clientAppId) } returns Optional.of(clientApplication)

        assertThrows(InsufficientClientApplicationScopeException::class.java) { underTest.verifyMail("A_TICKET") }
    }

    @Test
    internal fun `when client application does not exist`() {
        val clientAppId = ClientAppId("A_CLIENT_APP_ID")
        val account = AccountTestFixture.anAccount()
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(TicketFixture.ticketFor(verificationTicket.content, account.email, clientAppId.content))
        every { clientAccountRepository.findOne(clientAppId) } returns Optional.empty()

        assertThrows(InsufficientTicketException::class.java) { underTest.verifyMail("A_TICKET") }
    }

    @Test
    internal fun `when the account does not exist`() {
        val clientAppId = ClientAppId("A_CLIENT_APP_ID")
        val account = AccountTestFixture.anAccount()
        val clientApplication = ClientAppFixture.aClientApp(clientAppId).copy(scopes = Scopes.from(Scope.MAIL_VERIFY))
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.of(TicketFixture.ticketFor(verificationTicket.content, account.email, clientAppId.content))
        every { clientAccountRepository.findOne(clientAppId) } returns Optional.of(clientApplication)
        every { accountRepository.accountFor(account.email) } returns Optional.empty()

        assertThrows(InsufficientTicketException::class.java) { underTest.verifyMail("A_TICKET") }
    }

    @Test
    internal fun `when the ticket does not exist`() {
        val verificationTicket = VerificationTicket("A_TICKET")

        every { ticketRepository.loadFor(verificationTicket) } returns Optional.empty()

        assertThrows(InsufficientTicketException::class.java) { underTest.verifyMail("A_TICKET") }
    }

}