package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID
import javax.transaction.Transactional

class QuestionRepositoryTest(@Autowired val questionRepository: QuestionRepository) : IntegrationTest() {

  @Test
  fun `return Question by UUID`() {
    val questionSchemaUuid = UUID.fromString("574618c3-27f4-4dd2-94bb-6de74126ff22")
    val questionSchemaEntity = questionRepository.findByQuestionUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity).isNotNull
    assertThat(questionSchemaEntity?.questionUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionCode).isEqualTo("binge_drinking")
    assertThat(questionSchemaEntity?.answerGroup).isNotNull
    assertThat(questionSchemaEntity?.answerEntities?.size).isEqualTo(3)
  }

  @Transactional
  @Test
  fun `question with OASys mapping`() {
    val questionSchemaUuid = UUID.fromString("0941c5b2-f42d-4120-ad79-44954674fe00")
    val questionSchemaEntity = questionRepository.findByQuestionUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.oasysMappings?.size).isEqualTo(1)
    val oasysMapping = questionSchemaEntity?.oasysMappings?.first()
    assertThat(oasysMapping?.sectionCode).isEqualTo("ROSH")
    assertThat(oasysMapping?.logicalPage).isEqualTo(null)
    assertThat(oasysMapping?.questionCode).isEqualTo("R1.2.1.2_V2")
    assertThat(oasysMapping?.isFixed).isFalse
  }

  @Transactional
  @Test
  fun `question with OASys fixed field mapping`() {
    val questionSchemaUuid = UUID.fromString("5ca86a06-5472-4861-bd6a-a011780db49a")
    val questionSchemaEntity = questionRepository.findByQuestionUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.oasysMappings?.size).isEqualTo(1)
    val oasysMapping = questionSchemaEntity?.oasysMappings?.first()
    assertThat(oasysMapping?.sectionCode).isEqualTo("RSR")
    assertThat(oasysMapping?.logicalPage).isEqualTo(null)
    assertThat(oasysMapping?.questionCode).isEqualTo("1.8.2")
    assertThat(oasysMapping?.isFixed).isTrue
  }

  @Transactional
  @Test
  fun `question without OASys mapping`() {
    val questionSchemaUuid = UUID.fromString("63099aab-f852-4dd9-9179-16ee2218d0c6")
    val questionSchemaEntity = questionRepository.findByQuestionUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.oasysMappings?.size).isEqualTo(0)
  }
}
