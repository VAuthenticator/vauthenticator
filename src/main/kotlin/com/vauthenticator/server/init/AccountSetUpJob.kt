package com.vauthenticator.server.init

import com.vauthenticator.server.account.domain.Account
import com.vauthenticator.server.account.domain.AccountMandatoryAction
import com.vauthenticator.server.account.domain.AccountRepository
import com.vauthenticator.server.password.domain.VAuthenticatorPasswordEncoder
import com.vauthenticator.server.role.domain.Role
import com.vauthenticator.server.role.domain.RoleRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountSetUpJob(
    private val roleRepository: RoleRepository,
    private val accountRepository: AccountRepository,
    private val passwordEncoder: VAuthenticatorPasswordEncoder
) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        val userRole = Role("ROLE_USER", "Generic user role")
        val adminRole = Role("VAUTHENTICATOR_ADMIN", "VAuthenticator admin role")

        roleRepository.save(userRole)
        roleRepository.save(adminRole)

        accountRepository.save(
            Account(
                accountNonExpired = true,
                accountNonLocked = true,
                credentialsNonExpired = true,
                enabled = true,
                "admin@email.com",
                passwordEncoder.encode("secret"),
                authorities = setOf(userRole.name, adminRole.name),
                email = "admin@email.com",
                emailVerified = true,
                firstName = "Admin",
                lastName = "",
                birthDate = Optional.empty(),
                phone = Optional.empty(),
                locale = Optional.empty(),
                mandatoryAction = AccountMandatoryAction.NO_ACTION
            )
        )
    }

}