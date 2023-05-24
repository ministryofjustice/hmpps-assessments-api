package uk.gov.justice.digital.assessments.config

import jakarta.annotation.PostConstruct
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FlywayRefDataInitializer(@Qualifier("refDataDataSource") refDataDataSource: DataSource) {
  private val refDataDataSource: DataSource = refDataDataSource

  @PostConstruct
  fun migrateFlyway() {
    Flyway.configure()
      .schemas("hmppsassessmentsschemas")
      .dataSource(refDataDataSource)
      .locations("classpath:db/migration/refdata")
      .load().migrate()
  }
}
