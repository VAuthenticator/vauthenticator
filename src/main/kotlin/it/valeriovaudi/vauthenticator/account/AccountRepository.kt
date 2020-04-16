package it.valeriovaudi.vauthenticator.account

import com.fasterxml.jackson.databind.ObjectMapper
import it.valeriovaudi.vauthenticator.userdetails.NotParsableAccountDetails
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import java.util.*

interface AccountRepository {

    fun accountFor(username: String): Account

}

class CompositeAccountRepository(private val account: GetAccount,
                                 private val mongoUserRepository: MongoUserRepository) : AccountRepository {
    override fun accountFor(username: String): Account {
        return mongoUserRepository.findByUsername(username).map {
            val oidcProfileClaims = account.oidcProfileClaimsFor(username)

            Account(authorities = it.authorities,
                    username = it.username,
                    password = it.password,

                    // needed for email oidc profile
                    mail = username,

                    // needed for profile oidc profile
                    firstName = oidcProfileClaims.firstName,
                    lastName = oidcProfileClaims.lastName)
        }.orElse(null)
    }

}

interface MongoUserRepository : MongoRepository<MongoUser, String> {
    fun findByUsername(username: String): Optional<MongoUser>
}

@Document("user")
data class MongoUser(@Id var id: String,
                     var username: String,
                     var password: String,
                     var accountNonExpired: Boolean = true,
                     var accountNonLocked: Boolean = true,
                     var credentialsNonExpired: Boolean = true,
                     var enabled: Boolean = true,
                     var authorities: List<String>)

data class OidcProfileClaims(var firstName: String, var lastName: String)

@MessagingGateway
interface GetAccount {

    @Gateway(requestChannel = "getAccountInboundChannel",
            replyChannel = "getAccountOutboundChannel",
            replyTimeout = (60 * 1000).toLong())
    fun oidcProfileClaimsFor(username: String): OidcProfileClaims
}

class RabbitMessageAccountAdapter(private val objectMapper: ObjectMapper) {
    fun convert(securityAccountDetails: String) =
            try {
                objectMapper.readValue(securityAccountDetails, OidcProfileClaims::class.java)
            } catch (e: Exception) {
                throw NotParsableAccountDetails(e.message!!, e)
            }
}