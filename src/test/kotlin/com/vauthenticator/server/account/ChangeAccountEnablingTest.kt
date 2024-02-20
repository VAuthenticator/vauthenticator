package com.vauthenticator.server.account

import com.vauthenticator.server.account.repository.AccountRepository
import com.vauthenticator.server.support.AccountTestFixture.anAccount
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ChangeAccountEnablingTest {

    @MockK
    lateinit var accountRepository: AccountRepository

    lateinit var underTest: ChangeAccountEnabling

    @BeforeEach
    fun setUp() {
        underTest = ChangeAccountEnabling(accountRepository)
    }

    @Test
    fun `when we change account datas`() {
        val account = anAccount().copy(accountNonLocked = false, enabled = true, authorities = setOf("A_ROLE"))
        every { accountRepository.accountFor(account.email) } returns Optional.of(anAccount())
        every { accountRepository.save(account) } just runs

        underTest.execute(account.email, true, true, setOf("A_ROLE"))

        verify { accountRepository.accountFor(account.email) }
        verify { accountRepository.save(account) }
    }

    @Test
    fun `when the account is not found`() {
        val account = anAccount().copy(accountNonLocked = false, enabled = true, authorities = setOf("A_ROLE"))
        every { accountRepository.accountFor(account.email) } returns Optional.empty()

        underTest.execute(account.email, accountLocked = true, enabled = true, authorities = setOf("A_ROLE"))

        verify { accountRepository.accountFor(account.email) }
        verify(exactly = 0) { accountRepository.save(account) }
    }
}