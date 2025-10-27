package com.vauthenticator.server.role.api

import com.vauthenticator.server.role.domain.Group
import com.vauthenticator.server.role.domain.GroupRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class GroupEndPoint(private val groupRepository: GroupRepository) {

    @GetMapping("/api/groups")
    fun findAllGroups(): ResponseEntity<List<Group>> {
        val groups = groupRepository.findAll()
        return ResponseEntity.ok().body(groups)
    }

    @PutMapping("/api/groups")
    fun saveGroup(@RequestBody group: Group): ResponseEntity<Unit> {
        groupRepository.save(group)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/api/groups/{groupId}")
    fun deleteGroup() {
        TODO()
    }

    @PatchMapping("/api/groups/{groupId}/role/{roleId}")
    fun associateRoleToAGroup() {
        TODO()
    }

    @DeleteMapping("/api/groups/{groupId}/role/{roleId}")
    fun deAssociateRoleToAGroup() {
        TODO()
    }
}