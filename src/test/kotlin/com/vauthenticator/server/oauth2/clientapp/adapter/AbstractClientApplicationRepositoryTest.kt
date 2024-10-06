package com.vauthenticator.server.oauth2.clientapp.adapter

import com.vauthenticator.server.oauth2.clientapp.domain.ClientAppId
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplication
import com.vauthenticator.server.oauth2.clientapp.domain.ClientApplicationRepository
import com.vauthenticator.server.support.ClientAppFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

abstract class AbstractClientApplicationRepositoryTest {

    private lateinit var uut: ClientApplicationRepository

    abstract fun resetDatabase()
    abstract fun initUnitUnderTest() : ClientApplicationRepository

    @BeforeEach
    fun setUp() {
        uut = initUnitUnderTest()
        resetDatabase()
    }

    @Test
    fun `when find one client application by empty client id`() {
        val clientApp: Optional<ClientApplication> = uut.findOne(ClientAppId(""))
        val expected = Optional.empty<ClientApplication>()
        assertEquals(expected, clientApp)
    }

    @Test
    fun `when find one client application by client id that does not exist`() {
        val clientApp: Optional<ClientApplication> =
            uut.findOne(ClientAppId("not-existing-one"))
        val expected = Optional.empty<ClientApplication>()
        assertEquals(expected, clientApp)
    }

    @Test
    fun `when store, check if it exist and then delete a client application by client`() {
        val clientAppId = ClientAppId("client_id")
        val expected = ClientAppFixture.aClientApp(clientAppId)
        uut.save(expected)
        var actual = uut.findOne(clientAppId)

        assertEquals(Optional.of(expected), actual)

        uut.delete(clientAppId)
        actual = uut.findOne(clientAppId)

        assertEquals(Optional.empty<ClientApplication>(), actual)
    }

    @Test
    fun `when find all client applications`() {
        val clientAppId = ClientAppId("client_id")
        val expected = ClientAppFixture.aClientApp(clientAppId)
        uut.save(expected)
        val actual = uut.findAll()

        assertEquals(listOf(expected), actual)
    }

    @Test
    fun `when find an client application with zero authorities`() {
        val clientAppId = ClientAppId("client_id")
        val expected = ClientAppFixture.aClientApp(clientAppId)
        uut.save(expected)
        val actual = uut.findAll()

        assertEquals(listOf(expected), actual)
    }

}