package it.valeriovaudi.vauthenticator.keypair

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class KeyEndPont(
        @Value("\${vauthenticator.host}")  private val baseUrl: String,
        @Value("\${key.master-key}") private val masterKey: MasterKid,
        private val keyRepository: KeyRepository
) {

    @PostMapping("/keys")
    fun createKey() =
            keyRepository.createKeyFrom(masterKey)
                    .let { kid -> ResponseEntity.created(URI.create("$baseUrl/keys/$kid")).build<Unit>() }

}