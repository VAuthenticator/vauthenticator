package it.valeriovaudi.vauthenticator.account

import it.valeriovaudi.vauthenticator.account.AccountMapper.fromDocumentToDomain
import it.valeriovaudi.vauthenticator.account.AccountMapper.fromDomainToDocument
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.util.*

interface AccountRepository {
    fun accountFor(username: String): Optional<Account>
    fun create(account: Account)
}

class AccountRegistrationException(e: RuntimeException) : RuntimeException(e)


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
            ) as Map<String, Any>?)


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

class MongoAccountRepository(private val mongoTemplate: MongoTemplate) : AccountRepository {

    private val logger: Logger = LoggerFactory.getLogger(MongoAccountRepository::class.java)

    companion object {
        fun findById(id: String) = Query.query(Criteria.where("_id").`is`(id))
        fun findByUserName(username: String) = Query.query(Criteria.where("username").`is`(username))
        const val collectionName = "account"
    }

    override fun accountFor(userName: String): Optional<Account> =
            Optional.ofNullable(
                    mongoTemplate.findOne(findByUserName(userName), Document::class.java, collectionName)
            ).map { document -> fromDocumentToDomain(document) }

    override fun create(account: Account) =
            try {
                mongoTemplate.insert(fromDomainToDocument(account), collectionName); Unit
            } catch (e: RuntimeException) {
                logger.error(e.message, e)
                throw AccountRegistrationException(e)
            }

}

