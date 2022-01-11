# VAuthenticator

This project is actually a journey. This is a big evolution of the OAuth2 authorization server 
developed during my master thesis to an OpenID Connect authentication server.
In this new version I expand the initial project in order to use JWT token embracing OpenID Connect protocol,
all written in Kotlin based on Spring Boot 2.x and more over to the latest spring based oauth2/openid connect framework:
[spring-authorization-server](https://github.com/spring-projects-experimental/spring-authorization-server). 

## The Architecture

![](https://github.com/mrFlick72/vauthenticator/blob/master/images/vauthenticator-architecture.png)

## Features
Right now it is based, as said before to the latest version on spring oauth2/open id connect framework [spring-authorization-server](https://github.com/spring-projects-experimental/spring-authorization-server).

Client Applications, roles and openid projection of accounts are stored in a DynamoDB, while for authorization code, distributed session store and 
distributed cache, Redis store is my choice. 
VAuthenticator and the [account-service](https://github.com/mrFlick72/account-service) are synchronized with a secure AWS SQS channel, 
while the RSA key pair are created from KMS Customer Master Key stored on Dynamo, private key crypted via KMS of course.
VAuthenticator implements front_channel single logout openid connect specification and part of session management, in particular only 
the needed to support a better single logout experience is implemented, however I planned to implement full session management as well

# Additional content

[helm](docs/helm.md)