package uk.gov.justice.digital.assessments.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
  basePackages = ["uk.gov.justice.digital.assessments.jpa.repositories.refdata"],
  entityManagerFactoryRef = "refDataEntityManager",
  transactionManagerRef = "refDataTransactionManager",
)
class PersistenceRefDataConfiguration : WebMvcConfigurer {

  @Value("\${spring.hmppsassessmentsschemas.datasource.url}")
  private val refDataDataSourceUrl: String = ""

  @Bean
  fun refDataEntityManager(): LocalContainerEntityManagerFactoryBean {
    val em = LocalContainerEntityManagerFactoryBean()
    em.dataSource = refDataDataSource()
    em.setPackagesToScan(
      "uk.gov.justice.digital.assessments.jpa.entities.refdata",
    )
    val vendorAdapter = HibernateJpaVendorAdapter()
    em.jpaVendorAdapter = vendorAdapter
    return em
  }

  @Bean
  fun refDataTransactionManager(): PlatformTransactionManager {
    val transactionManager = JpaTransactionManager()
    transactionManager.entityManagerFactory = refDataEntityManager().getObject()
    return transactionManager
  }

  @Bean(name = ["refDataDataSource"])
  @ConfigurationProperties(prefix = "spring.hmppsassessmentsschemas.datasource")
  fun refDataDataSource(): DataSource? = DataSourceBuilder
    .create()
    .url(refDataDataSourceUrl)
    .build()
}
