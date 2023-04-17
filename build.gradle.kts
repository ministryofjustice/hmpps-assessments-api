
plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "4.8.5"
  kotlin("plugin.spring") version "1.8.20"
  kotlin("plugin.jpa") version "1.8.20"
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

dependencies {

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("org.springframework.boot:spring-boot-starter-aop")
  implementation("org.springframework.security:spring-security-oauth2-client")
  implementation("org.springframework.data:spring-data-redis:3.0.5")
  implementation("redis.clients:jedis:4.3.2")
  implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
  implementation("org.springdoc:springdoc-openapi-data-rest:1.7.0")
  implementation("org.springdoc:springdoc-openapi-kotlin:1.7.0")
  implementation("commons-io:commons-io:2.11.0")
  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("com.vladmihalcea:hibernate-types-52:2.21.1")
  implementation("com.beust:klaxon:5.6")
  implementation("com.google.code.gson:gson:2.10.1")
  implementation("com.google.guava:guava:31.1-jre")
  implementation("org.apache.commons:commons-lang3:3.12.0")
  implementation("org.postgresql:postgresql:42.6.0")
  implementation("org.flywaydb:flyway-core")
  runtimeOnly("com.h2database:h2:2.1.214")
  implementation("net.logstash.logback:logstash-logback-encoder:7.3")

  testRuntimeOnly("com.h2database:h2:2.1.214")
  testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    exclude(module = "mockito-core")
  }
  testImplementation("com.ninja-squad:springmockk:4.0.2")
  testImplementation("io.jsonwebtoken:jjwt:0.9.1")
  testImplementation("com.github.tomakehurst:wiremock-standalone:2.27.2")
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
