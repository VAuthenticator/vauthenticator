package it.valeriovaudi.vauthenticator.account

interface AccountRepository {

    fun accountFor(username: String): Account
}