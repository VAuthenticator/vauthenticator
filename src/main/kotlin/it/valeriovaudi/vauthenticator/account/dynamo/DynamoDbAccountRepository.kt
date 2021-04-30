package it.valeriovaudi.vauthenticator.account.dynamo

import it.valeriovaudi.vauthenticator.account.Account
import it.valeriovaudi.vauthenticator.account.AccountRepository
import it.valeriovaudi.vauthenticator.account.AccountAuthorities.addAuthorities
import it.valeriovaudi.vauthenticator.account.AccountAuthorities.removeAuthorities
import it.valeriovaudi.vauthenticator.account.dynamo.DynamoAccountConverter.fromDynamoToDomain
import it.valeriovaudi.vauthenticator.account.dynamo.DynamoAccountQueryFactory.deleteAccountRoleQueryFor
import it.valeriovaudi.vauthenticator.account.dynamo.DynamoAccountQueryFactory.findAccountQueryForUserName
import it.valeriovaudi.vauthenticator.account.dynamo.DynamoAccountQueryFactory.findAccountRoleByUserNameQueryFor
import it.valeriovaudi.vauthenticator.account.dynamo.DynamoAccountQueryFactory.findAllAccountQueryFor
import it.valeriovaudi.vauthenticator.account.dynamo.DynamoAccountQueryFactory.storeAccountQueryFor
import it.valeriovaudi.vauthenticator.account.dynamo.DynamoAccountQueryFactory.storeAccountRoleQueryFor
import it.valeriovaudi.vauthenticator.extentions.filterEmptyAccountMetadata
import it.valeriovaudi.vauthenticator.extentions.valueAsStringFor
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
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
            .flatMap {it.filterEmptyAccountMetadata()}
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
            storedAccountRolesSet = storedAccountRolesFor(account)
        )
    }

    private fun storeAccountWithRoles(
        account: Account,
        accountAuthoritiesSet: Set<String>,
        storedAccountRolesSet: Set<String>
    ) {
        storeAccountFrom(account)
        addAuthorities(accountAuthoritiesSet, storedAccountRolesSet) { authority ->
            storeAccountRoleFrom(account, authority)
        }
        removeAuthorities(storedAccountRolesSet, accountAuthoritiesSet) {
            deleteAccountRoleFrom(account, it)
        }
    }

    private fun storeAccountFrom(account: Account) {
        dynamoDbClient.putItem(
            storeAccountQueryFor(account, dynamoAccountTableName)
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

}