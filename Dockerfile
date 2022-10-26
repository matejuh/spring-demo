FROM eclipse-temurin:19-jre-focal

RUN mkdir /app
COPY ./build/libs/spring-demo-0.0.1-SNAPSHOT.jar /app/spring-demo.jar
WORKDIR /app
CMD ["java", "-jar", "spring-demo.jar"]
