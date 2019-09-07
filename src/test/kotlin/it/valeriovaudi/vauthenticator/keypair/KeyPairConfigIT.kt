package it.valeriovaudi.vauthenticator.keypair

import it.valeriovaudi.vauthenticator.account.AccountRepository
import it.valeriovaudi.vauthenticator.account.GetAccount
import it.valeriovaudi.vauthenticator.config.AccountRepositoryConfig
import it.valeriovaudi.vauthenticator.config.RepositoryConfig
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest(classes = [KeyPairConfigIT::class])
@RunWith(SpringRunner::class)
@TestPropertySource(properties = ["vauthenticator.keypair.repository.type=FILE_SYSTEM"])
class FileSystemKeyPairConfigIT {

    @Autowired
    lateinit var context: ApplicationContext

    @Test
    fun `repository is configured`() {
        val bean = context.getBean("keyRepository", FileKeyRepository::class.java)
        assertNotNull(bean)
    }
}

@SpringBootTest(classes = [KeyPairConfigIT::class])
@RunWith(SpringRunner::class)
@TestPropertySource(properties = ["vauthenticator.keypair.repository.type=AWS_S3"])
class S3KeyPairConfigIT {

    @Autowired
    lateinit var context: ApplicationContext

    @Test
    fun `repository is configured`() {
        val bean = context.getBean("keyRepository", S3KeyRepository::class.java)
        assertNotNull(bean)
    }
}

@SpringBootApplication
@Import(RepositoryConfig::class)
class KeyPairConfigIT {

    @Bean
    @Primary
    fun getAccount() = Mockito.mock(GetAccount::class.java)
}