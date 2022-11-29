package it.valeriovaudi.vauthenticator.mfa

import org.junit.Test
import org.junit.jupiter.api.Assertions.*

internal class EmailMFATest {

    @Test
    fun test(){
        val under = EmailMFA()
        val generateSecretKeyFor = under.generateSecretKeyFor("valerio.vaudi@gmail.com")
        println(generateSecretKeyFor)


    }
}