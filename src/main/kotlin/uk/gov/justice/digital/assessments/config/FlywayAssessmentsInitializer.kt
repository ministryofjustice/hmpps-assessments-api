package uk.gov.justice.digital.assessments.config

import org.flywaydb.core.Flyway

import javax.annotation.PostConstruct

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource


@Configuration
class FlywayAssessmentsInitializer(@Qualifier("assessmentsDataSource") assessmentsDataSource: DataSource) {
  private val assessmentsDataSource: DataSource = assessmentsDataSource

  @PostConstruct
  fun migrateFlyway() {
    Flyway.configure()
      .schemas("hmppsassessmentsapi")
      .dataSource(assessmentsDataSource)
      .locations("classpath:db/migration/assessments")
      .load().migrate()
  }

}