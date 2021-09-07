package uk.gov.justice.digital.assessments.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.orm.jpa.JpaTransactionManager

import org.springframework.transaction.PlatformTransactionManager

import org.springframework.context.annotation.Primary

import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.sql.DataSource


@Configuration
@EnableJpaRepositories(
  basePackages = ["uk.gov.justice.digital.assessments.jpa.repositories.assessments", "uk.gov.justice.digital.assessments.jpa.repositories.refdata"],
  entityManagerFactoryRef = "assessmentsEntityManager",
  transactionManagerRef = "assessmentsTransactionManager"
)
class PersistenceAssessmentsConfiguration : WebMvcConfigurer {
  @Autowired
  private val env: Environment? = null

  @Value("\${spring.hmppsassessmentsapi.datasource.url}")
  private val assessmentsDataSourceUrl: String = ""

  @Bean
  @Primary
  fun assessmentsEntityManager(): LocalContainerEntityManagerFactoryBean {
    val em = LocalContainerEntityManagerFactoryBean()
    em.dataSource = assessmentsDataSource()
    em.setPackagesToScan(
      "uk.gov.justice.digital.assessments.jpa.entities.assessments",
      "uk.gov.justice.digital.assessments.jpa.entities.refdata"
    )
    val vendorAdapter = HibernateJpaVendorAdapter()
    em.jpaVendorAdapter = vendorAdapter
    /*val properties = HashMap<String, Any?>()
    properties["hibernate.hbm2ddl.auto"] = env.getProperty("hibernate.hbm2ddl.auto")
    properties["hibernate.dialect"] = env.getProperty("hibernate.dialect")
    em.setJpaPropertyMap(properties)*/
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