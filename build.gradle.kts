
plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "3.3.6"
  kotlin("plugin.spring") version "1.5.10"
  kotlin("plugin.jpa") version "1.5.10"
}

allOpen {
  annotation("javax.persistence.Entity")
  annotation("javax.persistence.Embeddable")
  annotation("javax.persistence.MappedSuperclass")
}

configurations {
  implementation { exclude(mapOf("module" to "tomcat-jdbc")) }
}

dependencies {

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.security:spring-security-oauth2-client")
  implementation("org.springframework.data:spring-data-redis:2.5.4")
  implementation("redis.clients:jedis:3.6.3")
  implementation("org.springdoc:springdoc-openapi-ui:1.5.10")
  implementation("org.springdoc:springdoc-openapi-data-rest:1.5.10")
  implementation("org.springdoc:springdoc-openapi-kotlin:1.5.10")
  implementation("commons-io:commons-io:2.11.0")
  implementation("com.zaxxer:HikariCP:5.0.0")
  implementation("com.vladmihalcea:hibernate-types-52:2.12.1")
  implementation("com.beust:klaxon:5.5")
  implementation("com.google.code.gson:gson:2.8.8")
  implementation("com.google.guava:guava:30.1.1-jre")
  implementation("org.apache.commons:commons-lang3:3.12.0")
  implementation("org.postgresql:postgresql")
  runtimeOnly("com.h2database:h2:1.4.200")
  runtimeOnly("org.flywaydb:flyway-core:7.14.0")

  testRuntimeOnly("com.h2database:h2:1.4.200")
  testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    exclude(module = "mockito-core")
  }
  testImplementation("com.ninja-squad:springmockk:3.0.1")
  testImplementation("io.jsonwebtoken:jjwt:0.9.1")
  testImplementation("com.github.tomakehurst:wiremock-standalone:2.27.2")
}
