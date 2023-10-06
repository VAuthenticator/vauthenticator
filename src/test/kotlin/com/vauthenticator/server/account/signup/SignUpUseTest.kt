package com.vauthenticator.server.account.signup

import com.vauthenticator.server.account.AccountTestFixture.anAccount
import com.vauthenticator.server.account.Email
import com.vauthenticator.server.account.mailverification.SendVerifyMailChallenge
import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.account.welcome.SayWelcome
import com.vauthenticator.server.clientapp.ClientAppFixture.aClientApp
import com.vauthenticator.server.events.SignUpEvent
import com.vauthenticator.server.events.VAuthenticatorEventsDispatcher
import com.vauthenticator.server.oauth2.clientapp.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.ClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.Scope.Companion.SIGN_UP
import com.vauthenticator.server.oauth2.clientapp.Scopes
import com.vauthenticator.server.password.Password
import com.vauthenticator.server.password.PasswordPolicy
import com.vauthenticator.server.password.VAuthenticatorPasswordEncoder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
internal class SignUpUseTest {

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var clientAccountRepository: ClientApplicationRepository

    @MockK
    lateinit var passwordPolicy: PasswordPolicy

    @MockK
    lateinit var sayWelcome: SayWelcome

    @MockK
    lateinit var vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder

    @MockK
    lateinit var sendVerifyMailChallenge: SendVerifyMailChallenge

    @MockK
    lateinit var vAuthenticatorEventsDispatcher: VAuthenticatorEventsDispatcher

    private lateinit var underTest: SignUpUse

    @BeforeEach
    internal fun setUp() {
        underTest = SignUpUse(
            passwordPolicy,
            clientAccountRepository,
            accountRepository,
            vAuthenticatorPasswordEncoder,
            vAuthenticatorEventsDispatcher
        )
    }

    @Test
    internal fun `when a new account is created`() {
        val now = Instant.now()
        val clientAppId = ClientAppId("an_id")
        val aClientApp = aClientApp(clientAppId).copy(scopes = Scopes(setOf(SIGN_UP)))
        val account = anAccount().copy(authorities = setOf("AN_AUTHORITY"), password = "encrypted_secret")

        val signUpEvent = SignUpEvent(Email(account.email), clientAppId, now, Password("encrypted_secret"))

        every { passwordPolicy.accept(account.email, "secret") } just runs
        every { clientAccountRepository.findOne(clientAppId) } returns Optional.of(aClientApp)
        every { vAuthenticatorPasswordEncoder.encode("secret") } returns "encrypted_secret"
        every { accountRepository.create(account) } just runs
        every { sayWelcome.welcome(account.email) } just runs
        every { sendVerifyMailChallenge.sendVerifyMail("email@domain.com") } just runs
        every { vAuthenticatorEventsDispatcher.dispatch(signUpEvent) } just runs


        underTest.execute(clientAppId, account.copy(password = "secret"))

        verify { passwordPolicy.accept(account.email, "secret") }
        verify { clientAccountRepository.findOne(clientAppId) }
        verify { vAuthenticatorPasswordEncoder.encode("secret") }
        verify { accountRepository.create(account) }
        verify { sayWelcome.welcome(account.email) }
        verify { sendVerifyMailChallenge.sendVerifyMail("email@domain.com") }
        verify { vAuthenticatorEventsDispatcher.dispatch(signUpEvent) }
    }

}