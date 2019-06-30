# Vauthenticator

This project is actually a journey. This is a big evolution of the oAuth2 authorization server 
developed during the my master thesis to a OpenId Connect authentication server.
In this new version I expand the initial project in order to use JWT token embracing OpenId Connect protocol,
all written in Kotlin based on Spring Boot 2.x. 

## The Architecture

![](https://github.com/mrFlick72/vauthenticator/blob/master/images/vauhenticator-architecture.png)

## Sample Application

In order to try vauthenticator, in this repo you can find a no-configserver-configuration folder with all the basic 
configuration in order to start up the application without a config server and the needed keystore for the RSA key pair.
The only thing that you should do is provide these external configurations:

```properties
project.basedir=your project path
spring.cloud.bootstrap.location=${project.basedir}/no-configserver-configuration/bootstrap.yml;
```  

Since that the user data source in vauthenticator is provided by a microservice and not a classical datasource like: ldap, 
database and so on the repo provides a very simple account service suitable for testing under testable-account-service maven project.

Using these two applications: VAuthenticator started whit the properties like above and the testable-account-service project, 
you should be able to use vauthenticator for your sample application. The preconfigured user in testable-account-service is a 
user with `user` as username and `secret` as password 