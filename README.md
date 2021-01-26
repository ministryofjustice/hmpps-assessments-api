# HMPPS Assessments API

A Spring Boot app to manage Risk and Need Assessments across HMPPS.

[Swagger API documentation is available](https://offender-dev.aks-dev-1.studio-hosting.service.justice.gov.uk/swagger-ui.html)

## Dependencies
* Java JDK 11+
* An editor/IDE
* Gradle
* Docker
* Postgres
* OAuth  [(running in a container)](#oauth-security)
* Offender Assessments Updates Service
* [Offender Assessments API](https://github.com/ministryofjustice/offender-assessments-api-kotlin)
* [HMPPS Community API - Wiremock](https://github.com/ministryofjustice/community-api)
* [Court Case Service -Wiremock](https://github.com/ministryofjustice/court-case-service)

#### OAuth security
In order to run the service locally, [Nomis OAuth Service](https://github.com/ministryofjustice/nomis-oauth2-server/) is required. This can be run locally using the [docker-compose.yml](docker-compose.yml) file which will pull down the latest version.  From the command line run:

```
 docker-compose up 
```  

### Build service and run tests

This service is built using Gradle. In order to build the project from the command line and run the tests, use:
```  
./gradlew clean build  
```  
The created JAR file will be named "`hmpps-assessments-api-<yyyy-mm-dd>.jar`", using the date that the build takes place in the format `yyyy-mm-dd`.

### Start the application with H2 database

The configuration can be changed for the api to use an in-memory H2 database by using the spring boot profile `dev`. On the command line run:
```  
SPRING_PROFILES_ACTIVE=dev 
java -jar build/libs/hmpps-assessments-api-<yyyy-mm-dd>.jar  
```  

### Start the application with Postgres database
This configuration can be changed to use a Postgres database using the spring boot profile `postgres`.  

The service makes use of Postgres JSONB fields so it is advisable to run with postgres when making database changes to avoid issues with invlaid UUIDs breaking the build

On the command line run:
```  
SPRING_PROFILES_ACTIVE=postgres 
java -jar build/libs/hmpps-assessments-api-<yyyy-mm-dd>.jar  
```  

### Documentation
The generated documentation for the api can be viewed at http://localhost:8080/swagger-ui.html

## Code style & formatting
./gradlew ktlintApplyToIdea addKtlintFormatGitPreCommitHook
will apply ktlint styles to intellij and also add a pre-commit hook to format all changed kotlin files.

### Health

- `/ping`: will respond `pong` to all requests.  This should be used by dependent systems to check connectivity to   
  offender assessment service, rather than calling the `/health` endpoint.
- `/health`: provides information about the application health and its dependencies.  This should only be used  
  by offender assessment service health monitoring (e.g. pager duty) and not other systems who wish to find out the   
  state of offender assessment service.
- `/info`: provides information about the version of deployed application.  
  