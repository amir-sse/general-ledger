## LeoVegas
### General-Ledger
This is a financial bookkeeping software that is my solution to the LeoVegas coding challenge.

#### Development
##### How to run
To start the application in the dev profile, simply run:

    ./mvnw
    
By default, the application uses the port 8080 and it can be changed at the following path:

    src/main/resources/application.yml     

To run integration tests run:

    ./mvnw verify
    
to run unit tests run:

    ./mvnw test           

#### Building for production

##### Packaging as jar

To build the final jar for production, run:

    ./mvnw -Pprod clean package

To run the application using H2 database, run:

    java -jar target/*.jar
    
To run the application using PostgreSql database, run:

    java -jar -Dspring.profiles.active=prod ./target/*.jar
    
Configuration for the **prod** environment is at the following path
   
    src/main/resources/application-prod.yml 

#### Technologies
1. Spring-boot v2.1.11
1. Spring Data
1. JPA
1. Liquibase
1. H2 DB (dev profile)
1. PostgreSQL DB (prod profile)
1. Swagger API

#### Swagger UI
To open swagger UI for api testing go to this URL:
        
    localhost:*port*/swagger-ui.html
#### H2 Console
H2 console is accessible at this URL:

    localhost:*port*/h2-console    