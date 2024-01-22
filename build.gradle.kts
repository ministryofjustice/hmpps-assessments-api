
plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.13.0"
  kotlin("plugin.spring") version "1.9.22"
  kotlin("plugin.jpa") version "1.9.22"
}

allOpen {
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.Embeddable")
  annotation("jakarta.persistence.MappedSuperclass")
}

configurations {
  implementation { exclude(mapOf("module" to "tomcat-jdbc")) }
}

dependencyCheck {
  suppressionFiles.add("suppressions.xml")
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
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:2.1.1")
  implementation("org.springframework.data:spring-data-redis:3.2.1")
  implementation("redis.clients:jedis:5.1.0")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
  implementation("commons-io:commons-io:2.15.1")
  implementation("com.zaxxer:HikariCP:5.1.0")
  implementation("com.vladmihalcea:hibernate-types-60:2.21.1")
  implementation("com.beust:klaxon:5.6")
  implementation("com.google.code.gson:gson:2.10.1")
  implementation("com.google.guava:guava:33.0.0-jre")
  implementation("org.apache.commons:commons-lang3:3.14.0")
  implementation("org.postgresql:postgresql:42.7.1")
  implementation("org.flywaydb:flyway-core")
  implementation("net.logstash.logback:logstash-logback-encoder:7.4")

  testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    exclude(module = "mockito-core")
  }
  testImplementation("com.ninja-squad:springmockk:4.0.2")
  testImplementation("io.jsonwebtoken:jjwt-impl:0.12.3")
  testImplementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
  testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:3.0.1")
}
repositories {
  mavenCentral()
}
java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "17"
    }
  }
}
