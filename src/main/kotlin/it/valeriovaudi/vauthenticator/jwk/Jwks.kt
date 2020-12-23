package it.valeriovaudi.vauthenticator.jwk


typealias Jwk = Map<String, Any>

data class Jwks(val keys: List<Jwk>)