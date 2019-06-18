package it.valeriovaudi.vauthenticator.config

import it.valeriovaudi.vauthenticator.repository.FileKeyPairRepositoryConfig
import it.valeriovaudi.vauthenticator.repository.FileKeyRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepositoryConfig {

    @Bean
    @ConfigurationProperties(prefix = "key-store")
    fun fileKeyPairRepositoryConfig() = FileKeyPairRepositoryConfig()

    @Bean("keyRepository")
    @ConditionalOnProperty(value = ["vauthenticator.keypair.repository.type"], havingValue = "FILE_SYSTEM")
    fun fileKeyRepository() = FileKeyRepository(fileKeyPairRepositoryConfig())
}