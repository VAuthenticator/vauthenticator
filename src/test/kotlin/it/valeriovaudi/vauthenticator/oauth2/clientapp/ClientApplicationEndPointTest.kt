package it.valeriovaudi.vauthenticator.oauth2.clientapp

import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.DockerComposeContainer
import java.io.File


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class ClientApplicationEndPointTest {

    companion object {
        @ClassRule
        @JvmField
        val container: DockerComposeContainer<*> = DockerComposeContainer<Nothing>(File("src/test/resources/docker-compose.yml"))
                .withExposedService("postgres_1", 5432)

    }

    @LocalServerPort
    private lateinit var port: Integer

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate


    @Test
    fun `store a new clientapp`() {
        testRestTemplate.exchange("http://localhost:$port/vauthenticator/api/client-applications/clientAppId",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Unit.javaClass)

    }
}