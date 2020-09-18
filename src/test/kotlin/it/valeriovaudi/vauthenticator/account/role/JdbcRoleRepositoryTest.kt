package it.valeriovaudi.vauthenticator.account.role

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File


@Testcontainers
internal class JdbcRoleRepositoryTest {

    @Container
    val container: DockerComposeContainer<*> = DockerComposeContainer<Nothing>(File("src/test/resources/docker-compose.yml"))
            .withExposedService("postgres_1", 5432)

    lateinit var roleRepository: RoleRepository

    @BeforeEach
    fun setUp() {
        val serviceHost = container.getServiceHost("postgres_1", 5432)
        val servicePort = container.getServicePort("postgres_1", 5432)
        val dataSource = DataSourceBuilder.create()
                .url("jdbc:postgresql://$serviceHost:$servicePort/vauthenticator?user=root&password=root")
                .build()
        roleRepository = JdbcRoleRepository(JdbcTemplate(dataSource))
    }

    @Test
    internal fun findAllRoles() {
        val actual = roleRepository.findAll()
        val expected: List<Role> = listOf(Role("a_role", "A_ROLE"))

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `save a new role`() {
        roleRepository.save(Role("another_role", "ANOTHER_ROLE"))
        val expected = roleRepository.findAll()
        val actual = listOf(Role("a_role", "A_ROLE"), Role("another_role", "ANOTHER_ROLE"))

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `save a new role again`() {
        roleRepository.save(Role("another_role", "ANOTHER_ROLE"))
        roleRepository.save(Role("another_role", "ANOTHER_ROLE AGAIN"))
        val expected = roleRepository.findAll()
        val actual = listOf(Role("a_role", "A_ROLE"), Role("another_role", "ANOTHER_ROLE AGAIN"))

        Assertions.assertEquals(expected, actual)
    }

    @Test
    internal fun `delete a role`() {
        roleRepository.delete("a_role")
        val roles = roleRepository.findAll()

        Assertions.assertTrue { roles.isEmpty() }
    }
}