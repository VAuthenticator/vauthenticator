package it.valeriovaudi.vauthenticator.account

import it.valeriovaudi.vauthenticator.account.AccountMapper.fromDocumentToDomain
import it.valeriovaudi.vauthenticator.account.AccountMapper.fromDomainToDocument
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface AccountRepository {
    fun accountFor(username: String): Optional<Account>
    fun save(account: Account)
}

class AccountRegistrationException(e: RuntimeException) : RuntimeException(e)

@Transactional
class JdbcAccountRepository(private val jdbcTemplate: JdbcTemplate) : AccountRepository {

    val readByEmail: String = "SELECT * FROM account WHERE email=?"

    override fun accountFor(username: String): Optional<Account> {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(readByEmail, { rs, _ ->
                    Account(
                            accountNonExpired = rs.getBoolean("account_non_expired"),
                            accountNonLocked = rs.getBoolean("account_non_locked"),
                            credentialsNonExpired = rs.getBoolean("credentials_non_expired"),
                            enabled = rs.getBoolean("enabled"),

                            username = rs.getString("username"),
                            password = rs.getString("password"),
                            authorities = rs.getString("authorities").split(",").filter { it.isNotEmpty() } ,

                            // needed for email oidc profile
                            email = rs.getString("email"),
                            emailVerified = rs.getBoolean("email_verified"),

                            // needed for profile oidc profile
                            firstName = rs.getString("first_name"),
                            lastName = rs.getString("last_name")
                    )
                }, arrayOf(username))
        )
    }
    val insertQuery: String =
            """
                INSERT INTO ACCOUNT (account_non_expired,
                                     account_non_locked,
                                     credentials_non_expired,
                                     enabled,
                                     username,
                                     password,
                                     authorities,
                                     email,
                                     email_verified,
                                     first_name,
                                     last_name
                                    ) 
                                    VALUES (?,?,?,?,?,?,?,?,?,?,?)
            """.trimIndent()
    override fun save(account: Account) {
        jdbcTemplate.update(insertQuery,
                        account.accountNonExpired, account.accountNonLocked, account.credentialsNonExpired, account.enabled,
                        account.email, account.password, account.authorities.joinToString(","),
                        account.email, account.emailVerified, account.firstName, account.lastName
                )
    }

}


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

    override fun accountFor(username: String): Optional<Account> =
            Optional.ofNullable(
                    mongoTemplate.findOne(findByUserName(username), Document::class.java, collectionName)
            ).map { document -> fromDocumentToDomain(document) }

    override fun save(account: Account) =
            try {
                mongoTemplate.upsert(
                        findByUserName(account.username),
                        Update.fromDocument(fromDomainToDocument(account)),
                        collectionName);
                Unit
            } catch (e: RuntimeException) {
                logger.error(e.message, e)
                throw AccountRegistrationException(e)
            }

}

