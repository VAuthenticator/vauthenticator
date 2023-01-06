package com.vauthenticator.server.account.repository

import com.vauthenticator.server.account.Account
import com.vauthenticator.server.account.repository.AccountAuthorities.addAuthorities
import com.vauthenticator.server.account.repository.AccountAuthorities.removeAuthorities
import com.vauthenticator.server.account.repository.DynamoAccountConverter.fromDynamoToDomain
import com.vauthenticator.server.account.repository.DynamoAccountQueryFactory.deleteAccountRoleQueryFor
import com.vauthenticator.server.account.repository.DynamoAccountQueryFactory.findAccountQueryForUserName
import com.vauthenticator.server.account.repository.DynamoAccountQueryFactory.findAccountRoleByUserNameQueryFor
import com.vauthenticator.server.account.repository.DynamoAccountQueryFactory.findAllAccountQueryFor
import com.vauthenticator.server.account.repository.DynamoAccountQueryFactory.storeAccountQueryFor
import com.vauthenticator.server.account.repository.DynamoAccountQueryFactory.storeAccountRoleQueryFor
import com.vauthenticator.server.extentions.filterEmptyAccountMetadata
import com.vauthenticator.server.extentions.valueAsStringFor
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import java.util.*

class DynamoDbAccountRepository(
        private val dynamoDbClient: DynamoDbClient,
        private val dynamoAccountTableName: String,
        private val dynamoAccountRoleTableName: String
) : AccountRepository {

    override fun findAll(eagerRolesLoad: Boolean): List<Account> =
            findAllFromDynamo()
                    .map(this::convertAccountFrom)

    private fun findAllFromDynamo() =
            dynamoDbClient.scan(
                    findAllAccountQueryFor(dynamoAccountTableName)
            ).items()

    private fun convertAccountFrom(accountDynamoItem: MutableMap<String, AttributeValue>): Account {
        val authorities: List<String> = findAuthoritiesNameFor(accountDynamoItem.valueAsStringFor("user_name"))
        return fromDynamoToDomain(accountDynamoItem, authorities)
    }


    override fun accountFor(username: String): Optional<Account> {
        return Optional.ofNullable(findAccountFor(username))
                .flatMap { it.filterEmptyAccountMetadata() }
                .map { account ->
                    fromDynamoToDomain(
                            account,
                            findAuthoritiesNameFor(username)
                    )
                }
    }

    private fun findAuthoritiesNameFor(username: String): List<String> {
        return dynamoDbClient.query(
                findAccountRoleByUserNameQueryFor(username, dynamoAccountRoleTableName)
        ).items()
                .map { item -> item.valueAsStringFor("role_name") }
    }

    private fun findAccountFor(username: String) =
            dynamoDbClient.getItem(
                    findAccountQueryForUserName(username, dynamoAccountTableName)
            ).item()

    override fun save(account: Account) {
        storeAccountWithRoles(
                account = account,
                accountAuthoritiesSet = accountRolesFor(account),
                storedAccountRolesSet = storedAccountRolesFor(account),
                withUpsert = true
        )
    }

    private fun storeAccountWithRoles(
        account: Account,
        accountAuthoritiesSet: Set<String>,
        storedAccountRolesSet: Set<String>,
        withUpsert: Boolean
    ) {
        storeAccountFrom(account, withUpsert)
        addAuthorities(accountAuthoritiesSet, storedAccountRolesSet) { authority ->
            storeAccountRoleFrom(account, authority)
        }
        removeAuthorities(storedAccountRolesSet, accountAuthoritiesSet) {
            deleteAccountRoleFrom(account, it)
        }
    }

    private fun storeAccountFrom(account: Account, withUpsert: Boolean) {
        dynamoDbClient.putItem(
                storeAccountQueryFor(account, dynamoAccountTableName, withUpsert)
        )
    }

    private fun accountRolesFor(account: Account) = account.authorities.toSet()
    private fun storedAccountRolesFor(account: Account) =
            dynamoDbClient.query(
                    findAccountRoleByUserNameQueryFor(account.username, dynamoAccountRoleTableName)
            )
                    .items()
                    .map { item -> item.valueAsStringFor("role_name") }
                    .toSet()

    private fun storeAccountRoleFrom(account: Account, authority: String) {
        dynamoDbClient.putItem(
                storeAccountRoleQueryFor(account.username, authority, dynamoAccountRoleTableName)
        )
    }

    private fun deleteAccountRoleFrom(account: Account, roleName: String) =
            dynamoDbClient.deleteItem(deleteAccountRoleQueryFor(account.username, roleName, dynamoAccountRoleTableName))

    override fun create(account: Account) = try {
        storeAccountWithRoles(
                account = account,
                accountAuthoritiesSet = accountRolesFor(account),
                storedAccountRolesSet = emptySet(),
                withUpsert = false
        )
    } catch (e: ConditionalCheckFailedException) {
        throw AccountRegistrationException("account already created", e)
    }

}