FROM openjdk:21
# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container at /app
COPY target/radical-0.0.1-SNAPSHOT.jar /app

ENTRYPOINT ["java", "-jar", "radical-0.0.1-SNAPSHOT.jar"]