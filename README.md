## Backend for a service called "Filmorate"
#### Gradually, the service is enriched with new functionality 

Our application **can**:
1. Store information about films.
2. Put ratings from users.
3. Return the top-5 films recommended for viewing.
____
The application was written in Java. Code example:

```java
@SpringBootApplication
public class FilmorateApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }
}
``` 
*it is also a place where you can launch a project*
____
#### The following technologies were used in this project:
* REST-service with use Spring Boot
* Maven
* Lombok
* MockMvc
* This API was also tested using Postman
----
