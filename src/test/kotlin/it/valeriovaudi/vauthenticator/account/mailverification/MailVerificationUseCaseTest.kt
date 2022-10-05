package it.valeriovaudi.vauthenticator.account.mailverification

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import it.valeriovaudi.vauthenticator.account.AccountNotFoundException
import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.account.tiket.VerificationTicket
import it.valeriovaudi.vauthenticator.mail.MailSenderService
import it.valeriovaudi.vauthenticator.oauth2.clientapp.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
internal class MailVerificationUseCaseTest {

    @MockK
    lateinit var clientAccountRepository: ClientApplicationRepository

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var mailVerificationTicketFactory: MailVerificationTicketFactory

    @MockK
    lateinit var mailVerificationMailSender: MailSenderService

    private lateinit var underTest: MailVerificationUseCase

    @BeforeEach
    fun setup() {
        underTest = MailVerificationUseCase(
                clientAccountRepository,
                accountRepository,
                mailVerificationTicketFactory,
                mailVerificationMailSender,
                "https://vauthenticator.com"
        )
    }

    @Test
    internal fun `happy path`() {
        val clientAppId = ClientAppId("A_CLIENT_APP_ID")
        val account = anAccount()
        val clientApplication = ClientAppFixture.aClientApp(clientAppId).copy(scopes = Scopes.from(Scope.MAIL_VERIFY))
        val verificationTicket = VerificationTicket("A_TICKET")
        val requestContext = mapOf("verificationMailLink" to "https://vauthenticator.com/verify-mail/A_TICKET")


        every { clientAccountRepository.findOne(clientAppId) } returns Optional.of(clientApplication)
        every { accountRepository.accountFor(account.email) } returns Optional.of(account)
        every { mailVerificationTicketFactory.createTicketFor(account, clientApplication) } returns verificationTicket
        every { mailVerificationMailSender.sendFor(account, requestContext) } just runs

        underTest.sendVerifyMail(account.email, clientAppId)

        verify { mailVerificationMailSender.sendFor(account, requestContext) }
    }

    @Test
    internal fun `when client app does not have the correct scope`() {
        val clientAppId = ClientAppId("A_CLIENT_APP_ID")
        val account = anAccount()
        val clientApplication = ClientAppFixture.aClientApp(clientAppId)

        every { clientAccountRepository.findOne(clientAppId) } returns Optional.of(clientApplication)

        Assertions.assertThrows(InsufficientClientApplicationScopeException::class.java) {
            underTest.sendVerifyMail(account.email, clientAppId)
        }
    }

    @Test
    internal fun `when account does not exist`() {
        val mail = "anemail@test.com"
        val clientAppId = ClientAppId("A_CLIENT_APP_ID")
        val clientApplication = ClientAppFixture.aClientApp(clientAppId).copy(scopes = Scopes.from(Scope.MAIL_VERIFY))

        every { clientAccountRepository.findOne(clientAppId) } returns Optional.of(clientApplication)
        every { accountRepository.accountFor(mail) } returns Optional.empty()

        Assertions.assertThrows(AccountNotFoundException::class.java) {
            underTest.sendVerifyMail(mail, clientAppId)
        }
    }

}