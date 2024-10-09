FROM eclipse-temurin:17-jre-alpine
ADD target/*.jar /home/takehome-0.0.1-SNAPSHOT.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /home/takehome-0.0.1-SNAPSHOT.jar
