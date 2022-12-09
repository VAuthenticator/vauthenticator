package it.valeriovaudi.vauthenticator.keys

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class KeyEndPoint(
    @Value("\${key.master-key}") private val masterKey: String,
    private val keyRepository: KeyRepository
) {

    @GetMapping("/api/keys")
    fun loadAllKeys() =
        keyRepository.signatureKeys()
            .keys.map { mapOf("masterKey" to it.masterKid, "kid" to it.kid) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("/api/keys")
    fun createKey() =
        keyRepository.createKeyFrom(MasterKid(masterKey))
            .let { ResponseEntity.status(HttpStatus.CREATED).build<Unit>() }

    @DeleteMapping("/api/keys/{kid}")
    fun deleteKey(@PathVariable kid: String) =
        keyRepository.deleteKeyFor(Kid(kid))
            .let { ResponseEntity.noContent().build<Unit>() }

}