FROM eclipse-temurin:21.0.7_6-jre-alpine-3.21
COPY ./build/libs/umm-0.0.1-SNAPSHOT.jar /app/umm.jar
ENTRYPOINT ["java","-jar","/app/umm.jar"]