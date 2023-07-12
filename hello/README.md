# Service C

Simple service in Java 11 and Javalin Micro Framework.

Endpoints:

* "/"
* "/health"

## Develop

Open in your IDE and run *App.java*. Or build the project with Maven and run the jar file.

```bash
./mvnw clean package  
java -jar target/hello-1.0-SNAPSHOT-fat.jar
```

### Container image

With the provided Containerfile you can create a container image.