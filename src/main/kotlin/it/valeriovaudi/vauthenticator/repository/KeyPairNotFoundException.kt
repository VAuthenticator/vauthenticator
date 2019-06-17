package it.valeriovaudi.vauthenticator.repository

import java.lang.RuntimeException

class KeyPairNotFoundException(message: String) : RuntimeException(message)