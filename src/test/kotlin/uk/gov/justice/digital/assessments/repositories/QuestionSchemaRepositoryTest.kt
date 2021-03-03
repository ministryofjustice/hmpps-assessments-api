package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID
import javax.transaction.Transactional

@SqlGroup(
  Sql(scripts = ["classpath:referenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:referenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
open class QuestionSchemaRepositoryTest(@Autowired val questionSchemaRepository: QuestionSchemaRepository) : IntegrationTest() {

  @Test
  fun `return Question by UUID`() {
    val questionSchemaUuid = UUID.fromString("fd412ca8-d361-47ab-a189-7acb8ae0675b")
    val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionSchemaUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.answerSchemaGroup).isNotNull
    assertThat(questionSchemaEntity?.answerSchemaEntities?.size).isEqualTo(2)
  }

  @Transactional
  @Test
  open fun `question with OASys mapping`() {
    val questionSchemaUuid = UUID.fromString("a5830801-533c-4b9e-bab1-03272718d6dc")
    val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionSchemaUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.oasysMappings?.size).isEqualTo(1)
    val oasysMapping = questionSchemaEntity?.oasysMappings?.first()
    assertThat(oasysMapping?.sectionCode).isEqualTo("RSR")
    assertThat(oasysMapping?.logicalPage).isEqualTo(1)
    assertThat(oasysMapping?.questionCode).isEqualTo("RSR_02")
    assertThat(oasysMapping?.isFixed).isFalse()
  }

  @Transactional
  @Test
  open fun `question with OASys fixed field mapping`() {
    val questionSchemaUuid = UUID.fromString("a8e303f5-5f88-4343-94d1-a369ca1f86cb")
    val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionSchemaUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.oasysMappings?.size).isEqualTo(1)
    val oasysMapping = questionSchemaEntity?.oasysMappings?.first()
    assertThat(oasysMapping?.sectionCode).isEqualTo("OFFIN")
    assertThat(oasysMapping?.logicalPage).isEqualTo(null)
    assertThat(oasysMapping?.questionCode).isEqualTo("test_field")
    assertThat(oasysMapping?.isFixed).isTrue()
  }

  @Transactional
  @Test
  open fun `question without OASys mapping`() {
    val questionSchemaUuid = UUID.fromString("1948af63-07f2-4a8c-9e4c-0ec347bd6ba8")
    val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionSchemaUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.oasysMappings?.size).isEqualTo(0)
  }
}
