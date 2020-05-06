package it.valeriovaudi.vauthenticator.oauth2.clientapp

import it.valeriovaudi.vauthenticator.extentions.VAuthenticatorPasswordEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
class StoreClientApplication(private val clientApplicationRepository: ClientApplicationRepository,
                             private val passwordEncoder: VAuthenticatorPasswordEncoder) {
    fun store(aClientApp: ClientApplication, storeWithPassword: Boolean) {
        clientApplicationRepository.save(clientApplication(storeWithPassword, aClientApp))
    }

    private fun clientApplication(storeWithPassword: Boolean, aClientApp: ClientApplication): ClientApplication {
        return if (storeWithPassword) {
            aClientApp.copy(secret = Secret(passwordEncoder.encode(aClientApp.secret.content)))
        } else {
            clientApplicationRepository.findOne(clientAppId = aClientApp.clientAppId)
                    .map { aClientApp.copy(secret = it.secret) }
                    .orElseThrow()
        }
    }

    fun resetPassword(aClientAppId: ClientAppId, secret: Secret) {
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

    @Bean
    fun storeClientApplication(clientApplicationRepository: ClientApplicationRepository,
                               passwordEncoder: VAuthenticatorPasswordEncoder) =
            StoreClientApplication(clientApplicationRepository, passwordEncoder)
}