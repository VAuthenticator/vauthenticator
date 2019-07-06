package it.valeriovaudi.vauthenticator.userdetails


data class SecurityAccountDetails(var username: String, var password: String, var authorities: List<String>)