FROM openjdk:8-jdk-alpine

# Choose a base image that has Java 8 and PostgreSQL
# The database should have user/password postgres/postgres to save reconfiguring src/main/resources/application.properiets

ENV POSTGRESQL_USER=postgres POSTGRESQL_PASSWORD=postgres POSTGRESQL_DATABASE=keyprx

VOLUME /home/david/IdeaProjects

WORKDIR /home/david/IdeaProjects/keyprx/target

CMD ["/usr/bin/java", "-jar", "/home/david/IdeaProjects/keyprx/build/libs/keyprx-1.0.0.jar"]

EXPOSE 8080

