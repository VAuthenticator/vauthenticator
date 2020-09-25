package it.valeriovaudi.vauthenticator.security.userdetails

import it.valeriovaudi.TestAdditionalConfiguration
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File

//fixme
@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension::class)
@Import(TestAdditionalConfiguration::class)
@ContextConfiguration(initializers = [Initializer::class])
class AccountUserDetailsServiceIT {

    companion object {
        @Container
        val container: DockerComposeContainer<*> = DockerComposeContainer<Nothing>(File("src/test/resources/docker-compose.yml"))
                .withExposedService("rabbitmq_1", 5672)

    }

    @Autowired
    lateinit var accountUserDetailsService: AccountUserDetailsService

    @Test
    fun `error path`() {
        Assertions.assertThrows(UsernameNotFoundException::class.java) {
            accountUserDetailsService.loadUserByUsername("A_USER");
        }
    }
}


class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
        TestPropertyValues.of("spring.rabbitmq.port=${AccountUserDetailsServiceIT.container.getServicePort("rabbitmq_1", 5672)}")
                .applyTo(configurableApplicationContext.environment)
    }
}