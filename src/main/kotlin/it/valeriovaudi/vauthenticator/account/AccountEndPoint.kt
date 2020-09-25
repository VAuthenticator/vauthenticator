package it.valeriovaudi.vauthenticator.account

import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountEndPoint(private val accountRepository: AccountRepository) {

    @GetMapping("/api/accounts")
    fun findAll() =
            ok(
                    accountRepository.findAll()
                            .map { AccountConverter.fromDomainToAccountApiRepresentation(it) }


            )

    fun saveAccount() {
        TODO("")
    }
}

data class AccountApiRepresentation(
        val accountLocked: Boolean = true,
        val enabled: Boolean = true,
        var email: String = "",
        val authorities: List<String> = emptyList()
)