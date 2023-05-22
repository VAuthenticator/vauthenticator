package com.vauthenticator.server.role


val defaultRole = Role("ROLE_USER", "Default User Role")
val adminRole = Role("ROLE_ADMIN", "Admin User Role")

val roles = listOf<Role>(defaultRole, adminRole)

