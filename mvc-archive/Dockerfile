FROM openjdk:21
# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container at /app
COPY target/mvc-archive-0.0.1-SNAPSHOT.jar /app


ENTRYPOINT ["java", "-jar", "mvc-archive-0.0.1-SNAPSHOT.jar"]