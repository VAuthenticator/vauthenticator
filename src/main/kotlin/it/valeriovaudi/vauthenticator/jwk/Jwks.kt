package it.valeriovaudi.vauthenticator.jwk

import net.minidev.json.JSONObject

typealias Jwk = JSONObject

data class Jwks(val keys: List<Jwk>)