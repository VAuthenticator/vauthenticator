FROM openjdk:17

ADD target/management-ui.jar /usr/local/management-ui/

VOLUME /var/log/onlyone-portal

WORKDIR /usr/local/management-ui/

CMD ["java", "-jar", "vauthenticator-management-ui.jar"]