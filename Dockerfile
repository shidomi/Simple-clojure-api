FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/simple-cljapi-0.0.1-SNAPSHOT-standalone.jar /simple-cljapi/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/simple-cljapi/app.jar"]
