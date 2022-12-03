package it.valeriovaudi.vauthenticator.keypair

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class KeyEndPoint(
    @Value("\${key.master-key}") private val masterKey: MasterKid,
    private val keyRepository: KeyRepository
) {

    @GetMapping("/api/keys")
    fun loadAllKeys() =
        keyRepository.keys()
            .keys.map { mapOf("masterKey" to it.masterKid, "kid" to it.kid) }
            .let { ResponseEntity.ok(it) }


    @PostMapping("/api/keys")
    fun createKey() =
        keyRepository.createKeyFrom(masterKey)
            .let { ResponseEntity.status(HttpStatus.CREATED).build<Unit>() }

    @DeleteMapping("/api/keys")
    fun deleteKey(@RequestBody body: Map<String, String>) =
        keyRepository.deleteKeyFor(MasterKid(body["masterKey"]!!), Kid(body["kid"]!!))
            .let { ResponseEntity.noContent().build<Unit>() }

}