package it.valeriovaudi.vauthenticator.jwk

import it.valeriovaudi.vauthenticator.keypair.KeyPairFixture
import org.hamcrest.core.Is
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class JwkFactoryTest {

    val expected: String = """
        {"p":"61rwgP6GiRXcq4kjdMSYUIPgHUaRTo-246mvSJquvtLxagzfEVh4ie-FJ1rHX6yjXvKqJnHu3gfPYy_opTjuqoXz_FcHOyiIimBrQnjVsjsLWblhDnEEIFY5hpQsaHn4Mdk5VOCSlnccN5LjXVGzkxkhfwKrZz4tTJ5lXNkiep0","kty":"RSA","q":"yHP6L322ysp1pLAQCQz9WVmcYs4EE9Bz9-MjzHcniN26-6onu1pHm_xcD9bu_T7rMJSncufwp6QddHCXRpxj_-B37GKyrTQzv_TEynwzT_mkJkhEFg75_Rdp1tGNeN5Qdd2BvNLe37byU73LHLSR4h3CBSrVHftux9ULJcrXNvk","d":"k_AAAv-LceEeVQe8yzG0Jh_vb1hMgUv5dfTcVZS1qEAe2zMvVRX73JlKIjW2lkpIbAJYJJLLAQvgwp4Ll_fxkFRcK457BDK8m3jybXFklJHEG8YkOVEY6nIsGkNM47ujHSw2tTLdJilJRhyGZ4-4XKvEfMhJsko8hVaRxB5Ird_LPPcQEaFVS0-Vl7m-q0MFsEl877j_r-S8HypzFCQGtLAXH9O4yATIcg_Lh7_4TcJGPy5eH7xMjBgHsJ1tSuGQ4RhRRQ9AiqOLB5jPQmNybeKUPBDHqcB2pMtYGxkGfh3HByGT3n2M9UdSzxPUL6B7Go39R7DGlD5EZGyeXWKOQQ","e":"AQAB","use":"sig","kid":"ALIAS","qi":"GV0Yy1m_Lp7rX4q0CJOp4zhEm2uqmn2vFXuuFvLNrhsCm2s65UMdlsuVrv46ZwMu4bQHkekfkH6y1mxNfLq6NcB5J9aynRrcjg2t8B6qvtNhmWLQfhktwliIrrc6bDAdXdPQFH3oj8HAkSEF7M0ftG7jOXmjgNk63z0r1omtJW0","dp":"F4WxP9MwcwcCVQqRxpvXdUSY45Qn9agbaUmOQT1kdaHzitvad4tWEMMllbdW8dL5SQYLx2ffrcUuDwW8xLaZ8-ULa47vJBR7AQl97tjQ0s1f5wnGD4FwHiNAAJlSjuaRzbmeJ0OCoxMoeM1vd-YwgmUimeUUaG8oa93w14TEE2k","dq":"QSkyUu3S1ekqhPaWv9pxIPJ4W8YMnURN7m7v3C9_soYhml2dvNGEJbjLVHrSBZDGc2xfYoLzQAuhZ5CSHGoKN8uIMFeFLbiieevfbNMi-wRzz93-BA2qRhOuXB6Slo_uJ-JxoigKOz8FVyODOzmXkqGJyJQ74xswz9T5YrelbkE","n":"uEmrwUjvBkyhdtPzwIBNqRMDoGRCG4FDlB9ZILHgquv_FPaBEST5-BB12hpWsU03kjKbaiRCZA8S6-DPXA15_ehBodOieBnuEKE6uYegCFyI-bvbSc3aKOiqVNcwjFZ-CICjCYQLmwNkypIFc0knreHRJyLXxVDWfbGnMu2A3LEKEQwOQaPtQWwAIDTHLHDkFpvuTRPRxbkIRyB2aXMk9cs8sbeOElYIY5CffV3NV8AOvhVpu6IOaG_GgvL2btRrzCLpRHfOWR2ZUrmHZDUHx2tvYKnXgMgTPDqQvtjxWYzfSbJLtrNasOoeJ0Pq4jysZltianklFnFAozWCVEFgtQ"}
    """.trimIndent()

    @Test
    fun `happy path`() {
        val jwkFactory = JwkFactory()

        val content = KeyPairFixture.getFileContent("/keystore/keystore.jks")
        val keyPair = KeyPairFixture.keyPair(content)

        val createJwks = jwkFactory.createJwks(keyPair, "ALIAS")

        assertThat(createJwks.toJSONString(), Is.`is`(expected))
    }
}