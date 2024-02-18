package com.vauthenticator.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.vauthenticator.server.lambdas.AwsLambdaFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import software.amazon.awssdk.services.lambda.LambdaClient

@Configuration(proxyBeanMethods = false)
class LambdaConfig {

    @Bean
    fun lambdaFunction(
        redisTemplate: RedisTemplate<String, String>,
        objectMapper: ObjectMapper,
        client: LambdaClient
    ) = AwsLambdaFunction(redisTemplate, objectMapper, client)
}