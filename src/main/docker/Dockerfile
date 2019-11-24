FROM openjdk:12

ADD vauthenticator.jar /usr/local/vauthenticator/

VOLUME /var/log/onlyone-portal

WORKDIR /usr/local/vauthenticator/

CMD ["java", "-Xms512m", "-Xmx1g", "-jar", "vauthenticator.jar"]