package it.valeriovaudi.vauthenticator.account.role

import org.springframework.http.ResponseEntity.noContent
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RoleEndPoint(private val roleRepository: RoleRepository) {

    @PutMapping("/api/roles")
    fun saveRole(@RequestBody role: Role) =
            kotlin.run {
                roleRepository.save(role)
                noContent().build<Unit>()
            }


}