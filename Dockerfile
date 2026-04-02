FROM eclipse-temurin:21-jdk
EXPOSE 8081
COPY target/action-plan-*.jar /opt/app.jar
ENTRYPOINT ["java", "-jar", "/opt/app.jar"]
