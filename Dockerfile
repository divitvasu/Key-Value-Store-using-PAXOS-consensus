#BASE IMAGE for server
FROM bellsoft/liberica-openjdk-alpine-musl:17 AS server-build
COPY . /docker
WORKDIR /docker
RUN javac com/distributedsystems/*.java
CMD ["java", "com.distributedsystems.Server_app"]

#BASE IMAGE for client
FROM bellsoft/liberica-openjdk-alpine-musl:17 AS client-build
COPY . /docker
WORKDIR /docker
RUN javac com/distributedsystems/Client.java
CMD ["java", "com.distributedsystems.Client"]