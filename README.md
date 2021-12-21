# Media Now - Plan API

### Requirements

 - Java 17
 - Maven 3
 - Docker

### Build and run with Docker

```shell
mvn spring-boot:build-image
```

```shell
docker run -p 8080:8080 media-now:0.0.1-SNAPSHOT
```

### Up and Running

```shell
mvn spring-boot:run
```

```shell
mvn spring-boot:run -Dspring-boot.run.profiles=dev  // ---> loads `dev` profile
```

```shell
mvn spring-boot:run -Dspring-boot.run.profiles=prod  // ---> loads `prod` profile
```

### Unit tests

```shell
mvn clean test
```

### API docs

http://localhost:8080/swagger-ui.html

