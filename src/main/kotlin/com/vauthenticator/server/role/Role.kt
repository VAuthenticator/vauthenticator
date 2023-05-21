package com.vauthenticator.server.role

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.cache.CacheContentConverter

data class Role(val name: String, val description: String)

class DefaultRoleDeleteException(message: String) : RuntimeException(message)

class RoleCacheContentConverter(private val objectMapper: ObjectMapper) : CacheContentConverter<Role> {
    override fun getObjectFromCacheContentFor(cacheContent: String): Role =
        objectMapper.readValue(cacheContent, Map::class.java)
            .let {
                Role(
                    name = it["name"]!! as String,
                    description = it["description"]!! as String
                )
            }


    override fun loadableContentIntoCacheFor(source: Role): String =
        objectMapper.writeValueAsString(
            mapOf(
                "name" to source.name,
                "description" to source.description
            )
        )

}
