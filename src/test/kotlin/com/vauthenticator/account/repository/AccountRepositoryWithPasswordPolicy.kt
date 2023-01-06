package com.vauthenticator.account.repository

import com.vauthenticator.account.AccountTestFixture.anAccount
import com.vauthenticator.password.PasswordPolicy
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class AccountRepositoryWithPasswordPolicyTest {

    val account = anAccount()

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var passwordPolicy: PasswordPolicy

    lateinit var underTest: AccountRepository

    @BeforeEach
    internal fun setUp() {
        underTest = AccountRepositoryWithPasswordPolicy(
            accountRepository,
            passwordPolicy
        )
    }

    @Test
    fun create() {
        every { accountRepository.create(account) } just runs
        every { passwordPolicy.accept(account.password) } just runs
        underTest.create(account)
    }


    @Test
    fun save() {
        every { accountRepository.save(account) } just runs
        every { passwordPolicy.accept(account.password) } just runs
        underTest.save(account)
    }
}