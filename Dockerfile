FROM maven:3.8.6-eclipse-temurin-17-alpine

ARG MAVEN_VERSION=3.8.6
ARG USER_HOME_DIR="/root"

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

COPY mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
RUN chmod +x /usr/local/bin/mvn-entrypoint.sh
COPY settings-docker.xml /usr/share/maven/ref/

ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]
WORKDIR /uvl-acceptance-criteria
COPY . .
RUN _JAVA_OPTIONS="-Xmx2g" mvn package
RUN mvn site

EXPOSE 9640

CMD ["mvn", "exec:java"]
