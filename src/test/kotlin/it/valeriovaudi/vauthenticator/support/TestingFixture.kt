package it.valeriovaudi.vauthenticator.support

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames

object TestingFixture {

    private val key: ECKey = ECKeyGenerator(Curve.P_256)
            .keyID("123")
            .generate()

    fun simpleJwtFor(clientAppId: String, email: String = ""): String {
        val header = JWSHeader.Builder(JWSAlgorithm.ES256)
                .type(JOSEObjectType.JWT)
                .keyID("123")
                .build();


        var claim = JWTClaimsSet.Builder()
                .claim(IdTokenClaimNames.AZP, clientAppId)

        if(email.isNotBlank()){
            claim = claim.claim("user_name", email)
        }

        val payload = claim.build()

        val signedJWT = SignedJWT(header, payload)
        signedJWT.sign(ECDSASigner(key.toECPrivateKey()))

        return signedJWT.serialize()
    }


    fun loadFileFor(path : String) = String(ClassLoader.getSystemResourceAsStream(path).readAllBytes())
}