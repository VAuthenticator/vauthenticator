package it.valeriovaudi.vauthenticator.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import it.valeriovaudi.vauthenticator.account.MongoAccountRepository
import it.valeriovaudi.vauthenticator.account.MongoAccountRepositoryDelegate
import it.valeriovaudi.vauthenticator.keypair.FileKeyRepository
import it.valeriovaudi.vauthenticator.keypair.KeyPairConfig
import it.valeriovaudi.vauthenticator.keypair.S3Config
import it.valeriovaudi.vauthenticator.keypair.S3KeyRepository
import it.valeriovaudi.vauthenticator.openid.connect.userinfo.UserInfoFactory
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.MessageChannels

@Configuration
class RepositoryConfig {

    @Bean
    fun userInfoFactory(accountRepository: MongoAccountRepository) =
            UserInfoFactory(accountRepository)

    @Bean
    fun accountRepository(delegate: MongoAccountRepositoryDelegate) =
            MongoAccountRepository(delegate)

    @Bean
    @ConfigurationProperties(prefix = "key-store")
    fun keyPairConfig() = KeyPairConfig()

    @Bean
    @ConfigurationProperties(prefix = "key-store.aws.s3")
    fun s3Config() = S3Config()

    @Bean("keyRepository")
    @ConditionalOnProperty(value = ["vauthenticator.keypair.repository.type"], havingValue = "FILE_SYSTEM")
    fun fileKeyRepository() = FileKeyRepository(keyPairConfig())


    @Bean("keyRepository")
    @ConditionalOnProperty(value = ["vauthenticator.keypair.repository.type"], havingValue = "AWS_S3")
    fun s3KeyRepository(): S3KeyRepository {
        val s3Config = s3Config()
        val credentials = BasicAWSCredentials(s3Config.accessKey, s3Config.secretKey)

        val s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(AWSStaticCredentialsProvider(credentials))
                .withRegion(s3Config.region)
                .build()

        return S3KeyRepository(keyPairConfig(), s3Config, s3client)
    }

}

@Configuration
class AccountRepositoryConfig {

    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate

    @Bean("getAccountInboundQueue")
    fun getAccountInboundQueue() =
            Queue("getAccountInboundQueue", false)


    @Bean("getAccountInboundChannel")
    fun getAccountInboundChannel() =
            MessageChannels.direct().get()


    @Bean("getAccountOutboundChannel")
    fun getAccountOutboundChannel() =
            MessageChannels.direct().get()


/*    @Bean
    fun rabbitMessageAccountAdapter(objectMapper: ObjectMapper) =
            RabbitMessageAccountAdapter(objectMapper)

    @Bean
    fun getAccountPipelineConfig(rabbitMessageAccountAdapter: RabbitMessageAccountAdapter,
                                 getAccountInboundChannel: DirectChannel,
                                 getAccountOutboundChannel: DirectChannel) =
            IntegrationFlows.from(getAccountInboundChannel)
                    .handle(Amqp.outboundGateway(rabbitTemplate)
                            .routingKey("getAccountInboundQueue")
                            .returnChannel(getAccountOutboundChannel))
                    .transform(rabbitMessageAccountAdapter, "convert")
                    .get()*/
}