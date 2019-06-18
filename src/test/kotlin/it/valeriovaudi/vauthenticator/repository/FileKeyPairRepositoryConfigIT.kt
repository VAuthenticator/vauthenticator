package it.valeriovaudi.vauthenticator.repository

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
@TestPropertySource("classpath:repoconfig.yml")
class FileKeyPairRepositoryConfigIT {

    @Autowired
    lateinit var context: ApplicationContext

    @Test
    fun `repository is configured`() {
        val bean = context.getBean("keyRepository", FileKeyRepository::class.java)
        assertNotNull(bean)
    }
}