# VAuthenticator

This project is actually a journey. This is a big evolution of the OAuth2 authorization server 
developed during the my master thesis to an OpenID Connect authentication server.
In this new version I expand the initial project in order to use JWT token embracing OpenID Connect protocol,
all written in Kotlin based on Spring Boot 2.x. 

## The Architecture

![](https://github.com/mrFlick72/vauthenticator/blob/master/images/vauhenticator-architecture.png)

## Features
Right now the most tested version, is a installation on kubernetes, for local usage you can use minikube. 
The services that you need are account-service for user datasource, repository-service for keypair RSA certificate and configuration-server for confiuration.

The application is a Spring Cloud application build on the top of Spring Cloud Security in order to leverage the typical 
OAuth2 Authorization server. On the top of OAuth2 feature the VAuthenticator provide JWK endpoint for RSA key exchange 
with keystore storage provided from a my repository-service microservice, token enhancement of OpenID Connect id_token for client application 
that has `openid` as scope, well known OpenID Connect discovery endpoint, SSO via authorization_code, and global front channel logout. 

Client Application are stored in a Postgress Database, while for authorization code, distributed session store and 
distributed cache, a Redis store is my choice. 

At the moment, the application try to leverage the configuration from a configuration server.
The User datasource as said above is supposed to provided by an `account-service` cached in a monogo collection that store a falt reperesentation form 
openid connect point of view teh account data. VAuthenticator and the account-service are syncronized with a secure RabbitMq AMQP channel.

## Support

I have tested VAuthenticator in order to give SSO capability with my personal onlyone-portal, jenkins, Liferay 7.2 and Grafana 
 
## Next Features
* Password recovery system

## builds
in order to build the project you should start the doker-compose under src/test/resources folder.
The docker-compose file will start for you a postgress database listening on port 35432 with the needed database and tables. 
If you want change the port, you can do that as usual in docker, changing ports section in the docker-compose file and set `test.database.port` system properties 
in order to let know to the test suite the changed port 