package uk.gov.justice.digital.assessments.config

import org.flywaydb.core.Flyway

import javax.annotation.PostConstruct

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile
import javax.sql.DataSource


@Configuration
@DependsOn("flywayAssessmentsInitializer")
@Profile("dev", "test")
class FlywayAssessmentsTestDataInitializer(@Qualifier("assessmentsDataSource") assessmentsDataSource: DataSource) {
  private val assessmentsDataSource: DataSource = assessmentsDataSource

  @PostConstruct
  fun migrateFlyway() {
    Flyway.configure()
      .schemas("hmppsassessmentsapi")
      .dataSource(assessmentsDataSource)
      .locations("classpath:db/migration/assessments/testdata")
      .load().migrate()
  }

}