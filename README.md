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

**API:**

- Client Applications management  
- roles management
- account management
- sign up: admin:signup scope is required
- welcome mail: admin:welcome scope is required
- email verification: admin:mail-verify scope is required
- reset password: admin:reset-password scope is required

**Storage:**

- DynamoDB 
- Redis:
  - authorization code
  - distributed session store
  - distributed cache
- RSA key pair are created from KMS Customer Master Key stored on Dynamo, private key encrypted via KMS of course stored on Dynamo.

**General Feature:**

VAuthenticator implements front_channel single logout openid connect specification session management