package com.vauthenticator.server.keys

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Duration

@RestController
class KeyEndPoint(
    @Value("\${key.master-key}") private val masterKey: String,
    private val keyRepository: KeyRepository
) {

    @GetMapping("/api/keys")
    fun loadAllKeys() =
        keyRepository.signatureKeys()
            .keys.map { mapOf("masterKey" to it.masterKid, "kid" to it.kid, "ttl" to it.expirationDateTimestamp)}
            .let { ResponseEntity.ok(it) }


    @PostMapping("/api/keys")
    fun createKey() =
        keyRepository.createKeyFrom(MasterKid(masterKey))
            .let { ResponseEntity.status(HttpStatus.CREATED).build<Unit>() }

    @DeleteMapping("/api/keys")
    fun deleteKey(@RequestBody body: DeleteKeyRequest) =
        keyRepository.deleteKeyFor(
            Kid(body.kid),
            body.key_purpose,
            Duration.ofSeconds(body.key_ttl)
        )
            .let { ResponseEntity.noContent().build<Unit>() }

    @ExceptionHandler(KeyDeletionException::class)
    fun keyDeletionExceptionHandler(ex: KeyDeletionException) = ResponseEntity.badRequest().body(ex.message);
}

data class DeleteKeyRequest(val kid: String, val key_purpose: KeyPurpose, val key_ttl: Long)