package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionSchemaRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID
import javax.transaction.Transactional

class QuestionSchemaRepositoryTest(@Autowired val questionSchemaRepository: QuestionSchemaRepository) : IntegrationTest() {

  @Test
  fun `return Question by UUID`() {
    val questionSchemaUuid = UUID.fromString("574618c3-27f4-4dd2-94bb-6de74126ff22")
    val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity).isNotNull
    assertThat(questionSchemaEntity?.questionSchemaUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionCode).isEqualTo("binge_drinking")
    assertThat(questionSchemaEntity?.answerSchemaGroup).isNotNull
    assertThat(questionSchemaEntity?.answerSchemaEntities?.size).isEqualTo(3)
  }

  @Transactional
  @Test
  fun `question with OASys mapping`() {
    val questionSchemaUuid = UUID.fromString("0941c5b2-f42d-4120-ad79-44954674fe00")
    val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionSchemaUuid).isEqualTo(questionSchemaUuid)
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
    val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionSchemaUuid).isEqualTo(questionSchemaUuid)
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
    val questionSchemaEntity = questionSchemaRepository.findByQuestionSchemaUuid(questionSchemaUuid)
    assertThat(questionSchemaEntity?.questionSchemaUuid).isEqualTo(questionSchemaUuid)
    assertThat(questionSchemaEntity?.oasysMappings?.size).isEqualTo(0)
  }
}
