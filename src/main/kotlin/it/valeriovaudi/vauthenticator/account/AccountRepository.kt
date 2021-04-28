package it.valeriovaudi.vauthenticator.account

import it.valeriovaudi.vauthenticator.account.AccountAuthorities.addAuthorities
import it.valeriovaudi.vauthenticator.account.AccountAuthorities.removeAuthorities
import it.valeriovaudi.vauthenticator.account.AccountDynamoConverter.accountRoleFor
import it.valeriovaudi.vauthenticator.account.AccountDynamoConverter.accountToItemFor
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
        addAuthorities(accountAuthoritiesSet, storedAccountRolesSet) { authority ->
            jdbcTemplate.update("INSERT INTO ACCOUNT_ROLE(USERNAME, ROLE) VALUES (?,?)", account.username, authority)
        }
        removeAuthorities(storedAccountRolesSet, accountAuthoritiesSet) {
            jdbcTemplate.update("DELETE FROM ACCOUNT_ROLE WHERE USERNAME=? AND ROLE=?", account.username, it)
        }
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


}

object AccountAuthorities {
    fun removeAuthorities(
        storedAccountRolesSet: Set<String>,
        accountAuthoritiesSet: Set<String>,
        removeAuthorities: (String) -> Unit
    ) =
        storedAccountRolesSet.filter {
            !accountAuthoritiesSet.contains(it)
        }.forEach(removeAuthorities)


    fun addAuthorities(
        accountAuthoritiesSet: Set<String>,
        storedAccountRolesSet: Set<String>,
        addAuthorities: (String) -> Unit
    ) =
        accountAuthoritiesSet.filter {
            !storedAccountRolesSet.contains(it)
        }.forEach(addAuthorities)

}

object AccountDynamoConverter {
    fun accountFor(
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

    fun accountRoleFor(
        account: Account,
        roleName: String
    ) = mutableMapOf(
        "user_name" to AttributeValue.builder().s(account.username).build(),
        "role_name" to AttributeValue.builder().s(roleName).build()
    )

    fun accountToItemFor(account: Account) = mutableMapOf(
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
}

class DynamoDbAccountRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoAccountTableName: String,
    private val dynamoAccountRoleTableName: String
) : AccountRepository {
    override fun findAll(eagerRolesLoad: Boolean): List<Account> =
        findAllFromDynamo()
            .map(this::convertAccountFrom)

    private fun findAllFromDynamo() = dynamoDbClient.scan(
        ScanRequest.builder().tableName(dynamoAccountTableName).build()
    ).items()

    private fun convertAccountFrom(accountDynamoItem: MutableMap<String, AttributeValue>): Account {
        val authorities: List<String> = findAuthoritiesFor(accountDynamoItem["user_name"]?.s()!!)
        return AccountDynamoConverter.accountFor(accountDynamoItem, authorities)
    }


    override fun accountFor(username: String): Optional<Account> {
        val authorities = findAuthoritiesFor(username)
        return Optional.ofNullable(
            findAccountFrom(username)
                .let { AccountDynamoConverter.accountFor(it, authorities) }
        )
    }

    private fun findAuthoritiesFor(username: String): List<String> {
        return dynamoDbClient.query(
            QueryRequest.builder()
                .tableName(dynamoAccountRoleTableName)
                .keyConditionExpression("user_name = :username")
                .expressionAttributeValues(mutableMapOf(":username" to dynamoUserNameAttributeFor(username)))
                .build()
        )
            .items()
            .map { it["role_name"]?.s()!! }
    }

    private fun findAccountFrom(username: String) = dynamoDbClient.getItem(
        GetItemRequest.builder()
            .tableName(dynamoAccountTableName)
            .key(
                mutableMapOf(
                    "user_name" to dynamoUserNameAttributeFor(username)
                )
            )
            .build()
    ).item()

    private fun dynamoUserNameAttributeFor(username: String) = AttributeValue.builder().s(username).build()

    override fun save(account: Account) {
        storeAccountFrom(account)
        account.authorities
            .forEach { storeAccountRoleFrom(account, it) }
    }

    private fun storeAccountFrom(account: Account) {
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(dynamoAccountTableName)
                .item(accountToItemFor(account))
                .build()
        )
    }

    private fun storeAccountRoleFrom(account: Account, it: String) {
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(dynamoAccountRoleTableName)
                .item(accountRoleFor(account, it))
                .build()
        )
    }

}