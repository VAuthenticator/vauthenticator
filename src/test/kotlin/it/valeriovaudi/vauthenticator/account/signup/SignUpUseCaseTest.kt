package it.valeriovaudi.vauthenticator.account.signup

import it.valeriovaudi.vauthenticator.account.AccountTestFixture.anAccount
import it.valeriovaudi.vauthenticator.account.repository.AccountRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppFixture
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientAppId
import it.valeriovaudi.vauthenticator.oauth2.clientapp.ClientApplicationRepository
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scope.Companion.SIGN_UP
import it.valeriovaudi.vauthenticator.oauth2.clientapp.Scopes
import it.valeriovaudi.vauthenticator.security.VAuthenticatorPasswordEncoder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class SignUpUseCaseTest {

    @Mock
    lateinit var accountRepository: AccountRepository

    @Mock
    lateinit var clientAccountRepository: ClientApplicationRepository

    @Mock
    lateinit var signUpConfirmationMailSender: SignUpConfirmationMailSender

    @Mock
    lateinit var vAuthenticatorPasswordEncoder: VAuthenticatorPasswordEncoder

    @Test
    internal fun `when a new account is created`() {
        val underTest = SignUpUseCase(clientAccountRepository, accountRepository, signUpConfirmationMailSender, vAuthenticatorPasswordEncoder)

        val clientAppId = ClientAppId("an_id")
        val aClientApp = ClientAppFixture.aClientApp(clientAppId).copy(scopes = Scopes(setOf(SIGN_UP)))
        val account = anAccount().copy(authorities = listOf("AN_AUTHORITY"), password = "encrypted_secret")

        given(clientAccountRepository.findOne(clientAppId))
                .willReturn(Optional.of(aClientApp))

        given(vAuthenticatorPasswordEncoder.encode("secret"))
                .willReturn("encrypted_secret")

        underTest.execute(clientAppId, account.copy(password = "secret"))

        verify(accountRepository).create(account)
        verify(signUpConfirmationMailSender).sendConfirmation(account)
        verify(vAuthenticatorPasswordEncoder).encode("secret")
    }

    @Test
    internal fun `when a new account is not created due to client app does not support sign up`() {
        val underTest = SignUpUseCase(clientAccountRepository, accountRepository, signUpConfirmationMailSender, vAuthenticatorPasswordEncoder)

        val clientAppId = ClientAppId("an_id")
        val aClientApp = ClientAppFixture.aClientApp(clientAppId)
        val account = anAccount().copy(authorities = listOf("AN_AUTHORITY"))

        given(clientAccountRepository.findOne(clientAppId))
                .willReturn(Optional.of(aClientApp))

        Assertions.assertThrows(UnsupportedSignUpUseCaseException::class.java) {
            underTest.execute(clientAppId, account)
        }

    }

}