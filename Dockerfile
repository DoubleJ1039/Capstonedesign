FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN ./mvnw package
EXPOSE 8080
CMD ["java", "-jar", "target/pas-0.0.1-SNAPSHOT.jar"]
