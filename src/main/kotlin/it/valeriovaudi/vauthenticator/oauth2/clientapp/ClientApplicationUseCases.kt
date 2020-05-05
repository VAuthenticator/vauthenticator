package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.extentions.VAuthenticatorPasswordEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

class StoreClientApplication(private val clientApplicationRepository: ClientApplicationRepository,
                             private val passwordEncoder: VAuthenticatorPasswordEncoder) {
    fun store() {
        TODO("Not yet implemented")
    }


}

open class ReadClientApplication(private val clientApplicationRepository: ClientApplicationRepository) {
    open fun findOne(clientAppId: ClientAppId): Optional<ClientApplication> =
            clientApplicationRepository.findOne(clientAppId)
                    .map { it.copy(secret = Secret("*******")) }

    open fun findAll(): List<ClientApplication> =
            clientApplicationRepository.findAll()
                    .map { it.copy(secret = Secret("*******")) }
}

@Configuration
class ClientApplicationUseCasesConfig {

    @Bean
    fun readClientApplication(clientApplicationRepository: ClientApplicationRepository) =
            ReadClientApplication(clientApplicationRepository)
}