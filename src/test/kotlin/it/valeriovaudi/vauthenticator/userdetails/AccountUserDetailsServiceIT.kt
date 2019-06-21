package it.valeriovaudi.vauthenticator.userdetails

import it.valeriovaudi.TestAdditionalConfiguration
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Import
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.DockerComposeContainer
import java.io.File


@SpringBootTest
@RunWith(SpringRunner::class)
@Import(TestAdditionalConfiguration::class)
@ContextConfiguration(initializers = [Initializer::class])
class AccountUserDetailsServiceIT {

    companion object {
        @ClassRule
        @JvmField
        val container: DockerComposeContainer<*> = DockerComposeContainer<Nothing>(File("src/test/resources/docker-compose.yml"))
                .withExposedService("rabbitmq_1", 5672)

    }

    @Autowired
    lateinit var accountUserDetailsService: AccountUserDetailsService

    @Test(expected = UsernameNotFoundException::class)
    fun `error path`() {
        accountUserDetailsService.loadUserByUsername("A_USER");
    }
}


class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
        TestPropertyValues.of("spring.rabbitmq.port=${AccountUserDetailsServiceIT.container.getServicePort("rabbitmq_1", 5672)}")
                .applyTo(configurableApplicationContext.environment)
    }
}