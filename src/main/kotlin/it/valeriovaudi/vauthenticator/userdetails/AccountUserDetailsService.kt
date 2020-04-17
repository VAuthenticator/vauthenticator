package it.valeriovaudi.vauthenticator.userdetails

import it.valeriovaudi.vauthenticator.account.MongoAccountRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

open class AccountUserDetailsService(private val mongoUserRepository: MongoAccountRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String) =
            mongoUserRepository.accountFor(username)
                    .map {
                        println("Account: $it")
                        User(it.username,
                                it.password,
                                it.authorities.map { SimpleGrantedAuthority(it) })
                    }
                    .orElseThrow { UsernameNotFoundException("the user $username not found") }


}