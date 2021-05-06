# VAuthenticator

This project is actually a journey. This is a big evolution of the OAuth2 authorization server 
developed during my master thesis to an OpenID Connect authentication server.
In this new version I expand the initial project in order to use JWT token embracing OpenID Connect protocol,
all written in Kotlin based on Spring Boot 2.x and more over to the latest spring based oauth2/openid connect framework:
[spring-authorization-server](https://github.com/spring-projects-experimental/spring-authorization-server). 

## The Architecture

![](https://github.com/mrFlick72/vauthenticator/blob/master/images/vauhenticator-architecture.png)

## Features
Right now it is based, as said before to the latest version on spring oauth2/open id connect framework [spring-authorization-server](https://github.com/spring-projects-experimental/spring-authorization-server).
The services that you need are repository-service for keypair RSA certificate and a configuration-server for configurations.

Client Applications, roles and openid projection of accounts are stored in a DynamoDB, while for authorization code, distributed session store and 
distributed cache, a Redis store is my choice. 
At the moment, the application try to leverage the configuration from a configuration server.  
VAuthenticator and the account-service are syncronized with a secure RabbitMq AMQP channel.

## Support
I have tested VAuthenticator in order to give SSO capability with my personal onlyone-portal, jenkins, Liferay 7.2 and Grafana 
 
## Next Features
* Password recovery system

## builds
in order to build the project you should have an AWS account with a couple of environment variables 
STAGING_DYNAMO_DB_ROLE_TABLE_NAME, STAGING_DYNAMO_DB_ACCOUNT_TABLE_NAME, 
STAGING_DYNAMO_DB_ACCOUNT_ROLE_TABLE_NAME, STAGING_DYNAMO_DB_CLIENT_APPLICATION_TABLE_NAME pointing to existing dynamodb tables