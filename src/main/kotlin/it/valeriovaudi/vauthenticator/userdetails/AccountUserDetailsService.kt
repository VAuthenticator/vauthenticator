package it.valeriovaudi.vauthenticator.userdetails

import it.valeriovaudi.vauthenticator.account.MongoUserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class AccountUserDetailsService(private val mongoUserRepository: MongoUserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String) =
            mongoUserRepository.findByUsername(username)
                    .map { User(it.username, it.password, it.authorities.map { SimpleGrantedAuthority(it) }) }
                    .orElseThrow { UsernameNotFoundException("the user $username not found") }


}