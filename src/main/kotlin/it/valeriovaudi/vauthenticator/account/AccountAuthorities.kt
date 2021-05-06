package it.valeriovaudi.vauthenticator.account

object AccountAuthorities {
    fun removeAuthorities(
        storedAccountRolesSet: Set<String>,
        accountAuthoritiesSet: Set<String>,
        removeAuthoritiesFn: (String) -> Unit
    ) =
        storedAccountRolesSet.filter {
            !accountAuthoritiesSet.contains(it)
        }.forEach(removeAuthoritiesFn)


    fun addAuthorities(
        accountAuthoritiesSet: Set<String>,
        storedAccountRolesSet: Set<String>,
        addAuthoritiesFn: (String) -> Unit
    ) =
        accountAuthoritiesSet.filter {
            !storedAccountRolesSet.contains(it)
        }.forEach(addAuthoritiesFn)

}