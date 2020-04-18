package it.valeriovaudi.vauthenticator.account

import it.valeriovaudi.vauthenticator.account.AccountMapper.fromDocumentToDomain
import it.valeriovaudi.vauthenticator.account.AccountMapper.fromDomainToDocument
import it.valeriovaudi.vauthenticator.account.AccountRepository.Companion.collectionName
import it.valeriovaudi.vauthenticator.account.AccountRepository.Companion.findById
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import java.util.*

data class Account(var accountNonExpired: Boolean = true,
                   var accountNonLocked: Boolean = true,
                   var credentialsNonExpired: Boolean = true,
                   var enabled: Boolean = true,

                   var username: String,
                   var password: String,
                   var authorities: List<String>,

                   var sub: String,

        // needed for email oidc profile
                   var email: String,
        //todo should be changed when account
                   var emailVerified: Boolean = true,

        // needed for profile oidc profile

                   var firstName: String,
                   var lastName: String
)

object AccountMapper {
    fun fromDomainToDocument(account: Account) =
            Document(mutableMapOf(
                    "_id" to account.sub,

                    "accountNonExpired" to account.accountNonExpired,
                    "accountNonLocked" to account.accountNonLocked,
                    "credentialsNonExpired" to account.credentialsNonExpired,
                    "enabled" to account.enabled,

                    "username" to account.username,
                    "password" to account.password,
                    "authorities" to account.authorities,

                    "sub" to account.sub,

                    "email" to account.email,
                    "emailVerified" to true,

                    "firstName" to account.firstName,
                    "lastName" to account.lastName
            )
                    as Map<String, Any>?)

    fun fromDocumentToDomain(document: Document) =
            Account(accountNonExpired = document.getBoolean("accountNonExpired"),
                    accountNonLocked = document.getBoolean("accountNonLocked"),
                    credentialsNonExpired = document.getBoolean("credentialsNonExpired"),
                    enabled = document.getBoolean("enabled"),

                    username = document.getString("username"),
                    password = document.getString("password"),
                    authorities = document.getList("authorities", String::class.java),

                    sub = document.getString("sub"),

                    email = document.getString("email"),
                    emailVerified = document.getBoolean("emailVerified"),

                    firstName = document.getString("firstName"),
                    lastName = document.getString("lastName")
            )
}

interface AccountRepository {
    fun accountFor(username: String): Optional<Account>
    fun save(account: Account)

    companion object {
        fun findById(id: String) = Query.query(Criteria.where("_id").`is`(id))
        const val collectionName = "account"
    }
}

class MongoAccountRepository(private val mongoTemplate: MongoTemplate) : AccountRepository {
    override fun accountFor(username: String): Optional<Account> =
            Optional.ofNullable(
                    mongoTemplate.findOne(Query.query(Criteria.where("username").`is`(username)), Document::class.java, collectionName)
            ).map { document -> fromDocumentToDomain(document) }

    override fun save(account: Account) {
        mongoTemplate.upsert(findById(account.sub),
                Update.fromDocument(fromDomainToDocument(account)),
                collectionName
        )
    }
}

