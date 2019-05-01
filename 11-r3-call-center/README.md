# How to launch

## Build project from source, build docker container

```cmd
mvn clean install
```

Requirements:
* Maven 3 
* JDK 8
* Docker (with enabled property "Expose daemon on tcp//:localhost:2375 without TLS")
 
This will download all dependencies, run tests, build jar file, build docker container and deploy it to docker.

## Start docker containers

```
docker-compose up
```

# Used technologies

* Spring Boot - quick configuration, embedded Tomcat server.
* Spring MVC - for REST controllers.
* Neo4j as NoSQL database.
* Spring Data Neo4j - for generation Dao objects.

Why Neo4j was chosen:
* Fast, modern database, can be used in cluster out of the box.
* Cypher (Neo4j query language) suits well for this particular task (Search for relationships between data)

# Algorithm
Using Cypher we select following data: employee, expertise, overallQualification, queryQualification

overallQualification - how many expertises particular employee has
queryQualification - how many of them present in the current batch

We sort selected data by two keys: queryQual and then overallQual. Doing this we resolve batch in optimal way 
(See test CallServiceIntTest.pickIsOptimalWithinBatch)

# Tests

Application is covered with integration tests: Application fully starts (including embedded neo4j instance).
Test flow:
Dao -> database
database -> Dao -> BusinessLogic -> Controller

# Scaling
Set up cluster of Neo4j instances and several instances of application (All state is stored in db)