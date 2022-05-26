
plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "4.2.0-beta-4"
  kotlin("plugin.spring") version "1.6.21"
  kotlin("plugin.jpa") version "1.6.21"
}

allOpen {
  annotation("javax.persistence.Entity")
  annotation("javax.persistence.Embeddable")
  annotation("javax.persistence.MappedSuperclass")
}

configurations {
  implementation { exclude(mapOf("module" to "tomcat-jdbc")) }
}

dependencyCheck {
  suppressionFiles.add("suppressions.xml")
}

ext["log4j2.version"] = "2.17.1" // this can be removed once spring boot 2.5.8 is added to the dps gradle plugin
dependencies {

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.security:spring-security-oauth2-client")
  implementation("org.springframework.data:spring-data-redis:2.6.4")
  implementation("redis.clients:jedis:3.7.0")
  implementation("org.springdoc:springdoc-openapi-ui:1.6.7")
  implementation("org.springdoc:springdoc-openapi-data-rest:1.6.7")
  implementation("org.springdoc:springdoc-openapi-kotlin:1.6.7")
  implementation("commons-io:commons-io:2.11.0")
  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("com.vladmihalcea:hibernate-types-52:2.16.1")
  implementation("com.beust:klaxon:5.6")
  implementation("com.google.code.gson:gson:2.9.0")
  implementation("com.google.guava:guava:31.1-jre")
  implementation("org.apache.commons:commons-lang3:3.12.0")
  implementation("org.postgresql:postgresql:42.3.4")
  implementation("org.flywaydb:flyway-core:8.5.9")
  runtimeOnly("com.h2database:h2:1.4.200")
  implementation("net.logstash.logback:logstash-logback-encoder:7.1.1")

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
repositories {
  mavenCentral()
}
