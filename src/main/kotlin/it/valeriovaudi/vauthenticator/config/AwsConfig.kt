package it.valeriovaudi.vauthenticator.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider
import java.util.*

class AwsConfig {

    @Bean("awsCredentialsProvider")
    @ConditionalOnProperty("iamUserAwsCredentialsProvider", havingValue = "true")
    fun iamUserAwsCredentialsProvider(): AwsCredentialsProvider {
        return EnvironmentVariableCredentialsProvider.create()
    }


    @Bean("awsCredentialsProvider")
    @ConditionalOnProperty("iamServiceAccountAwsCredentialsProvider", havingValue = "true")
    fun iamServiceAccountAwsCredentialsProvider(): AwsCredentialsProvider {
        return WebIdentityTokenFileCredentialsProvider.builder().roleSessionName("vauthenticator-${UUID.randomUUID()}").build()
    }

}