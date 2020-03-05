package it.valeriovaudi.vauthenticator.jwk

import it.valeriovaudi.vauthenticator.keypair.KeyRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class JwksEndPoint(@Value("\${key-store.keyStorePairAlias:}") private var alias: String,
                   private val keyRepository: KeyRepository,
                   private val jwkFactory: JwkFactory) {

    @GetMapping("/.well-known/jwks.json")
    fun jwks(): ResponseEntity<Jwks> {
        val keyPair = keyRepository.getKeyPair()
        val createJwks = jwkFactory.createJwks(keyPair, alias)
        return ok(Jwks(listOf(createJwks)))
    }
}