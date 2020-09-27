package it.valeriovaudi.vauthenticator.account

import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*

@RestController
class AccountEndPoint(private val accountRepository: AccountRepository) {

    @GetMapping("/api/accounts")
    fun findAll() =
            ok(
                    accountRepository.findAll()
                            .map { AccountConverter.fromDomainToAccountApiRepresentation(it) }
            )

    @GetMapping("/api/accounts/{email}/email")
    fun findAccountFor(@PathVariable email: String) =
            ok(
                    accountRepository.accountFor(email)
                            .map { AccountConverter.fromDomainToAccountApiRepresentation(it) }
            )

    @PutMapping("/api/accounts/{email}/email")
    fun saveAccount(@PathVariable email: String,
                    @RequestBody representation: AccountApiRepresentation) =
            accountRepository.accountFor(email)
                    .map {account ->
                        accountRepository.save(
                                account.copy(
                                        accountNonLocked = !representation.accountLocked,
                                        enabled = representation.enabled,
                                        authorities = representation.authorities
                                )
                        )
                        noContent().build<Unit>()
                    }
                    .orElse(notFound().build<Unit>())
}

data class AccountApiRepresentation(
        val accountLocked: Boolean = true,
        val enabled: Boolean = true,
        var email: String = "",
        val authorities: List<String> = emptyList()
)