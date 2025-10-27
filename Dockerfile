FROM gradle:9-jdk21-alpine AS builder

FROM eclipse-temurin:25-jre-alpine AS runtime

FROM builder AS build
WORKDIR /app
ADD . .
RUN gradle --no-daemon assemble

FROM builder AS development
RUN apk add --no-cache curl
WORKDIR /app

FROM runtime AS production
LABEL maintainer="HMPPS Digital Studio <info@digital.justice.gov.uk>"
ARG BUILD_NUMBER
ENV BUILD_NUMBER=${BUILD_NUMBER:-1_0_0}
RUN apk add --no-cache tzdata curl
ENV TZ=Europe/London
RUN cp "/usr/share/zoneinfo/$TZ" /etc/localtime && echo "$TZ" > /etc/timezone
RUN addgroup --gid 2000 --system appgroup && \
    adduser --uid 2000 --system appuser --ingroup appgroup
WORKDIR /app
COPY --from=build --chown=appuser:appgroup /app/build/libs/hmpps-assessments-api*.jar /app/app.jar
COPY --from=build --chown=appuser:appgroup /app/build/libs/applicationinsights-agent*.jar /app/agent.jar
COPY --from=build --chown=appuser:appgroup /app/applicationinsights.json /app
USER 2000
#ENTRYPOINT ["java", "-XX:+AlwaysActAsServerClassMachine", "-javaagent:/app/agent.jar", "-jar", "/app/app.jar"]
ENTRYPOINT ["java", "-Dcom.sun.management.jmxremote.local.only=false", "-javaagent:/app/agent.jar", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
