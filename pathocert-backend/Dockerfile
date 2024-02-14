#
FROM gradle:7.2.0-jdk11 as backend-builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle distTar --no-daemon
RUN ls build/distributions

FROM openjdk:11-jdk-buster
RUN apt-get update
RUN apt update
RUN apt-get install software-properties-common -y
WORKDIR /app/
COPY --from=backend-builder /home/gradle/src/build/distributions/backend-0.2.tar ./backend.tar
RUN tar -xvf backend.tar
ENTRYPOINT ["./backend-0.2/bin/backend"]
