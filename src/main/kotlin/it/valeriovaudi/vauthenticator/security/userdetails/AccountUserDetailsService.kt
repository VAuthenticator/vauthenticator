package it.valeriovaudi.vauthenticator.security.userdetails

import it.valeriovaudi.vauthenticator.account.AccountRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

open class AccountUserDetailsService(private val userRepository: AccountRepository) : UserDetailsService {
    private val logger: Logger = LoggerFactory.getLogger(AccountUserDetailsService::class.java)

    override fun loadUserByUsername(username: String) =
            userRepository.accountFor(username)
                    .map {
                        logger.info("Account found for $username username")
                        User(it.username,
                                it.password,
                                it.authorities.map { SimpleGrantedAuthority(it) })
                    }
                    .orElseThrow {
                        logger.error("Account not found for $username username")
                        UsernameNotFoundException("the user $username not found")
                    }
}