package it.valeriovaudi.vauthenticator.keypair

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
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

@SpringBootTest
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