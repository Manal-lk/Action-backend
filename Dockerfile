FROM eclipse-temurin:21-jdk
EXPOSE 8081
COPY "target\Action-Plan-*.jar" /opt/app.jar
ENTRYPOINT ["java", "-jar", "/opt/app.jar"]
