# HMPPS Assessments API

A Spring Boot app to manage Risk and Need Assessments across HMPPS.

[Swagger API documentation is available](https://api-dev.hmpps-assessments.service.justice.gov.uk/swagger-ui/index.html)

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

### Run local instance from Intellij with downstream services from dev environment

- Go to edit configurations for Spring Boot in run/debug configurations for `HmppsAssessmentApiApplication` in Intellij
- Set active profile to `postgres`
- Add the following environment variables as a single semicolon delimited line to point to the dev downstream services: 
  - OAUTH_ENDPOINT_URL=https://sign-in-dev.hmpps.service.justice.gov.uk/auth;
  - ASSESSMENT_UPDATE_API_BASE_URL=https://asmnt-updte-dev.aks-dev-1.studio-hosting.service.justice.gov.uk;
  - COMMUNITY_API_BASE_URL=https://community-api-secure.test.delius.probation.hmpps.dsd.io/;
  - ASSESSMENT_API_BASE_URL=https://offender-dev.aks-dev-1.studio-hosting.service.justice.gov.uk;
  - ASSESS_RISKS_AND_NEEDS_API_BASE_URL=https://assess-risks-and-needs-dev.hmpps.service.justice.gov.uk/;
  - AUDIT_BASE_URL=https://audit-api-dev.hmpps.service.justice.gov.uk;
  - ASSESSMENT_API_ID=\<speak to another dev to get this\>;
  - ASSESSMENT_API_CLIENT_SECRET=\<speak to another dev to get this\>;
  - ASSESS_RISKS_AND_NEEDS_API_ID=\<speak to another dev to get this\>;
  - ASSESS_RISKS_AND_NEEDS_API_CLIENT_SECRET=\<speak to another dev to get this\>;
  - COMMUNITY_API_CLIENT_ID=\<speak to another dev to get this\>;
  - COMMUNITY_API_CLIENT_SECRET=\<speak to another dev to get this\>;
  - AUDIT_CLIENT_ID=\<speak to another dev to get this\>;
  - AUDIT_CLIENT_SECRET=\<speak to another dev to get this\>;
- Run `docker-compose up redis postgres` to start the cache layer and database
- Start `HmppsAssessmentApiApplication` in Intellij
- To create an oauth token do the following in Postman
  - Set authorization `basic auth` and get credentials from another dev
  - POST https://sign-in-dev.hmpps.service.justice.gov.uk/auth/oauth/token?grant_type=password&username=<speak_to_another_dev_to_get_this>&password=<speak_to_another_dev_to_get_this> in Postman or similar
  - The response will contain an `access_token` which will used as your `bearer token` when sending a request to a `hmpps-assessments-api` endpoint.

  
