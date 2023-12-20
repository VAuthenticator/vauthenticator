FROM amazoncorretto:21

ADD target/vauthenticator.jar /usr/local/vauthenticator/

VOLUME /var/log/onlyone-portal

WORKDIR /usr/local/vauthenticator/

CMD ["java", "-jar", "vauthenticator.jar"]