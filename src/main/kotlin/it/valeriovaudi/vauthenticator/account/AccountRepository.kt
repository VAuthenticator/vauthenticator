package it.valeriovaudi.vauthenticator.account

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.sql.ResultSet
import java.util.*

interface AccountRepository {
    fun findAll(eagerRolesLoad: Boolean = false): List<Account>
    fun accountFor(username: String): Optional<Account>
    fun save(account: Account)
}

class AccountRegistrationException(e: RuntimeException) : RuntimeException(e)

val roleLoader: (ResultSet, Int) -> String = { rs, _ -> rs.getString("role") }
val accountLoader: (ResultSet, Int) -> Account = { rs, _ ->
    Account(
        accountNonExpired = rs.getBoolean("account_non_expired"),
        accountNonLocked = rs.getBoolean("account_non_locked"),
        credentialsNonExpired = rs.getBoolean("credentials_non_expired"),
        enabled = rs.getBoolean("enabled"),

        username = rs.getString("username"),
        password = rs.getString("password"),
        authorities = emptyList(),

        // needed for email oidc profile
        email = rs.getString("email"),
        emailVerified = rs.getBoolean("email_verified"),

        // needed for profile oidc profile
        firstName = rs.getString("first_name"),
        lastName = rs.getString("last_name")
    )
}
const val findAllAccountRoleFor: String = "SELECT * FROM account_role where username=?"
const val findAll: String = "SELECT * FROM account"
const val readByEmail: String = "SELECT * FROM account WHERE email=?"
val insertQuery: String =
    """
                INSERT INTO ACCOUNT (account_non_expired,
                                     account_non_locked,
                                     credentials_non_expired,
                                     enabled,
                                     username,
                                     password,
                                     email,
                                     email_verified,
                                     first_name,
                                     last_name
                                    ) 
                                    VALUES (?,?,?,?,?,?,?,?,?,?)
                                    ON CONFLICT (email) DO UPDATE SET
                                     account_non_expired=?,
                                     account_non_locked=?,
                                     credentials_non_expired=?,
                                     enabled=?,
                                     password=?,
                                     email_verified=?,
                                     first_name=?,
                                     last_name=?
            """.trimIndent()


@Transactional
class JdbcAccountRepository(private val jdbcTemplate: JdbcTemplate) : AccountRepository {

    @Transactional(readOnly = true)
    override fun findAll(eagerRolesLoad: Boolean): List<Account> =
        jdbcTemplate.query(findAll, accountLoader)
            .map {
                if (eagerRolesLoad) {
                    it.copy(authorities = jdbcTemplate.query(findAllAccountRoleFor, roleLoader, arrayOf(it.email)))
                } else {
                    it
                }
            }

    @Transactional(readOnly = true)
    override fun accountFor(username: String): Optional<Account> {
        return jdbcTemplate.query(findAllAccountRoleFor, roleLoader, arrayOf(username))
            .let { roles ->
                Optional.ofNullable(
                    jdbcTemplate.queryForObject(readByEmail, accountLoader, arrayOf(username))
                ).map { it.copy(authorities = roles) }
            }
    }

    override fun save(account: Account) =
        storeAccountWithRoles(
            account = account,
            accountAuthoritiesSet = accountRolesFor(account),
            storedAccountRolesSet = storedRolesFor(account)
        )

    private fun storeAccountWithRoles(
        account: Account,
        accountAuthoritiesSet: Set<String>,
        storedAccountRolesSet: Set<String>
    ) {
        saveAccountFor(account)
        addAuthorities(accountAuthoritiesSet, storedAccountRolesSet, account)
        removeAuthorities(storedAccountRolesSet, accountAuthoritiesSet, account)
    }

    private fun accountRolesFor(account: Account) = account.authorities.toSet()

    private fun storedRolesFor(account: Account) =
        jdbcTemplate.query("SELECT * FROM  ACCOUNT_ROLE WHERE USERNAME=?", arrayOf(account.email), roleLoader)
            .toSet()

    private fun saveAccountFor(account: Account) {
        jdbcTemplate.update(
            insertQuery,
            account.accountNonExpired, account.accountNonLocked, account.credentialsNonExpired, account.enabled,
            account.email, account.password,
            account.email, account.emailVerified, account.firstName, account.lastName,

            account.accountNonExpired, account.accountNonLocked, account.credentialsNonExpired, account.enabled,
            account.password, account.emailVerified, account.firstName, account.lastName
        )
    }

    private fun removeAuthorities(
        storedAccountRolesSet: Set<String>,
        accountAuthoritiesSet: Set<String>,
        account: Account
    ) =
        storedAccountRolesSet.filter {
            !accountAuthoritiesSet.contains(it)
        }.forEach {
            jdbcTemplate.update("DELETE FROM ACCOUNT_ROLE WHERE USERNAME=? AND ROLE=?", account.username, it)
        }


    private fun addAuthorities(
        accountAuthoritiesSet: Set<String>,
        storedAccountRolesSet: Set<String>,
        account: Account
    ) =
        accountAuthoritiesSet.filter {
            !storedAccountRolesSet.contains(it)
        }.forEach { authority ->
            jdbcTemplate.update("INSERT INTO ACCOUNT_ROLE(USERNAME, ROLE) VALUES (?,?)", account.username, authority)
        }

}


class DynamoDbAccountRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoAccountTableName: String,
    private val dynamoAccountRoleTableName: String
) : AccountRepository {
    override fun findAll(eagerRolesLoad: Boolean): List<Account> =

        dynamoDbClient.scan(
            ScanRequest.builder().tableName(dynamoAccountTableName).build()
        ).items()
            .map {
                val authorities: List<String> = authoritiesFor(it["user_name"]?.s()!!)
                accountFor(it, authorities)
            }

    private fun accountFor(
        it: MutableMap<String, AttributeValue>,
        authorities: List<String>
    ) = Account(
        accountNonExpired = it["accountNonExpired"]?.bool()!!,
        accountNonLocked = it["accountNonLocked"]?.bool()!!,
        credentialsNonExpired = it["credentialsNonExpired"]?.bool()!!,
        enabled = it["enabled"]?.bool()!!,
        username = it["user_name"]?.s()!!,
        password = it["password"]?.s()!!,
        email = it["email"]?.s()!!,
        emailVerified = it["emailVerified"]?.bool()!!,
        firstName = it["firstName"]?.s()!!,
        lastName = it["lastName"]?.s()!!,
        authorities = authorities
    )


    override fun accountFor(username: String): Optional<Account> {
        val authorities = authoritiesFor(username)

        return Optional.ofNullable(
            dynamoDbClient.getItem(
                GetItemRequest.builder()
                    .tableName(dynamoAccountTableName)
                    .key(
                        mutableMapOf(
                            "user_name" to AttributeValue.builder().s(username).build()
                        )
                    )
                    .build()
            ).item()
                .let {
                    accountFor(it, authorities)
                }
        )
    }

    private fun authoritiesFor(username: String): List<String> {
        return dynamoDbClient.query(
            QueryRequest.builder()
                .tableName(dynamoAccountRoleTableName)
                .keyConditionExpression("user_name = :username")
                .expressionAttributeValues(mutableMapOf(":username" to AttributeValue.builder().s(username).build()))
                .build()
        )
            .items()
            .map { it["role_name"]?.s()!! }
    }

    override fun save(account: Account) {
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(dynamoAccountTableName)
                .item(
                    mutableMapOf(
                        "accountNonExpired" to AttributeValue.builder().bool(account.accountNonExpired).build(),
                        "accountNonLocked" to AttributeValue.builder().bool(account.accountNonLocked).build(),
                        "credentialsNonExpired" to AttributeValue.builder().bool(account.credentialsNonExpired).build(),
                        "enabled" to AttributeValue.builder().bool(account.enabled).build(),
                        "user_name" to AttributeValue.builder().s(account.username).build(),
                        "password" to AttributeValue.builder().s(account.password).build(),
                        "email" to AttributeValue.builder().s(account.email).build(),
                        "emailVerified" to AttributeValue.builder().bool(account.emailVerified).build(),
                        "firstName" to AttributeValue.builder().s(account.firstName).build(),
                        "lastName" to AttributeValue.builder().s(account.lastName).build()
                    )
                )
                .build()
        )

        account.authorities
            .forEach {
                dynamoDbClient.putItem(
                    PutItemRequest.builder()
                        .tableName(dynamoAccountRoleTableName)
                        .item(
                            mutableMapOf(
                                "user_name" to AttributeValue.builder().s(account.username).build(),
                                "role_name" to AttributeValue.builder().s(it).build()
                            )
                        )
                        .build()
                )
            }

    }

}