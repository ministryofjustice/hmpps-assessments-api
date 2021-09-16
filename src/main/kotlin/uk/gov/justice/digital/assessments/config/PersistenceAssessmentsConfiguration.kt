package uk.gov.justice.digital.assessments.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
  basePackages = ["uk.gov.justice.digital.assessments.jpa.repositories.assessments"],
  entityManagerFactoryRef = "assessmentsEntityManager",
  transactionManagerRef = "assessmentsTransactionManager"
)
class PersistenceAssessmentsConfiguration : WebMvcConfigurer {

  @Value("\${spring.hmppsassessmentsapi.datasource.url}")
  private val assessmentsDataSourceUrl: String = ""

  @Bean
  @Primary
  fun assessmentsEntityManager(): LocalContainerEntityManagerFactoryBean {
    val em = LocalContainerEntityManagerFactoryBean()
    em.dataSource = assessmentsDataSource()
    em.setPackagesToScan(
      "uk.gov.justice.digital.assessments.jpa.entities.assessments"
    )
    val vendorAdapter = HibernateJpaVendorAdapter()
    em.jpaVendorAdapter = vendorAdapter
    return em
  }

  @Primary
  @Bean
  fun assessmentsTransactionManager(): PlatformTransactionManager {
    val transactionManager = JpaTransactionManager()
    transactionManager.entityManagerFactory = assessmentsEntityManager().getObject()
    return transactionManager
  }

  @Primary
  @Bean(name = ["assessmentsDataSource"])
  @ConfigurationProperties(prefix = "spring.hmppsassessmentsapi.datasource")
  fun assessmentsDataSource(): DataSource? {
    return DataSourceBuilder
      .create()
      .url(assessmentsDataSourceUrl)
      .build()
  }
}
