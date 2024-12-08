package com.vauthenticator.server.init

import com.vauthenticator.server.oauth2.clientapp.adapter.cache.CachedClientApplicationRepository
import com.vauthenticator.server.oauth2.clientapp.domain.*
import com.vauthenticator.server.oauth2.clientapp.domain.AuthorizedGrantType.CLIENT_CREDENTIALS
import com.vauthenticator.server.oauth2.clientapp.domain.Scope.Companion.AVAILABLE_SCOPES
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Service

@Service
class AccountSetUpJob() : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        TODO("Not yet implemented")
    }

}