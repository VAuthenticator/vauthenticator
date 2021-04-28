package it.valeriovaudi.vauthenticator.account

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.util.*

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
        val authorities: List<String> = findAuthoritiesNameFor(accountDynamoItem["user_name"]?.s()!!)
        return AccountDynamoConverter.accountFor(accountDynamoItem, authorities)
    }


    override fun accountFor(username: String): Optional<Account> {
        val authorities = findAuthoritiesNameFor(username)
        return Optional.ofNullable(
            findAccountFrom(username)
                .let { AccountDynamoConverter.accountFor(it, authorities) }
        )
    }

    private fun findAuthoritiesNameFor(username: String): List<String> {
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
        storeAccountWithRoles(
            account = account,
            accountAuthoritiesSet = accountRolesFor(account),
            storedAccountRolesSet = storedAccountRolesFor(account)
        )

    }

    private fun storeAccountWithRoles(
        account: Account,
        accountAuthoritiesSet: Set<String>,
        storedAccountRolesSet: Set<String>
    ) {
        storeAccountFrom(account)
        AccountAuthorities.addAuthorities(accountAuthoritiesSet, storedAccountRolesSet) { authority ->
            storeAccountRoleFrom(account, authority)
        }
        AccountAuthorities.removeAuthorities(storedAccountRolesSet, accountAuthoritiesSet) {
            deleteAccountRoleFrom(account, it)
        }
    }


    private fun storeAccountFrom(account: Account) {
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(dynamoAccountTableName)
                .item(AccountDynamoConverter.accountToItemFor(account))
                .build()
        )
    }

    private fun accountRolesFor(account: Account) = account.authorities.toSet()
    private fun storedAccountRolesFor(account: Account) =
        dynamoDbClient.query(
            QueryRequest.builder()
                .tableName(dynamoAccountRoleTableName)
                .keyConditionExpression("user_name = :username")
                .expressionAttributeValues(mutableMapOf(":username" to dynamoUserNameAttributeFor(account.username)))
                .build()
        )
            .items()
            .map { it["role_name"]?.s()!! }
            .toSet()


    private fun storeAccountRoleFrom(account: Account, it: String) {
        dynamoDbClient.putItem(
            PutItemRequest.builder()
                .tableName(dynamoAccountRoleTableName)
                .item(AccountDynamoConverter.accountRoleFor(account, it))
                .build()
        )
    }

    private fun deleteAccountRoleFrom(account: Account, roleName: String) {
        val deleteItemRequest = DeleteItemRequest.builder().tableName(dynamoAccountRoleTableName)
            .key(
                mutableMapOf(
                    "user_name" to AttributeValue.builder().s(account.username).build(),
                    "role_name" to AttributeValue.builder().s(roleName).build()
                )
            )
            .build()
        dynamoDbClient.deleteItem(deleteItemRequest)
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