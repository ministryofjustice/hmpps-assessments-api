package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.AnswerDto
import uk.gov.justice.digital.assessments.api.AnswersDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@DisplayName("Oasys Assessment Update Service Tests")
class OasysAssessmentUpdateServiceTest() {
  private val questionService: QuestionService = mockk()
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()

  private val oasysAssessmentUpdateService = OasysAssessmentUpdateService(
    questionService,
    assessmentUpdateRestClient,
    assessmentSchemaService
  )

  private val question1Uuid = UUID.randomUUID()
  private val question2Uuid = UUID.randomUUID()
  private val question3Uuid = UUID.randomUUID()
  private val answer1Uuid = UUID.randomUUID()
  private val answer2Uuid = UUID.randomUUID()
  private val answer3Uuid = UUID.randomUUID()
  private val oasysOffenderPk = 1L
  private val oasysSetPk = 1L
  private val episodeId1 = 1L
  private val oasysAssessmentType = OasysAssessmentType.SHORT_FORM_PSR

  @BeforeEach
  fun setup() {
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
  }

  @Test
  fun `update OASys if OASysSet stored against episode`() {
    every { questionService.getAllQuestions() } returns setupQuestionCodes()
    every {
      assessmentUpdateRestClient.updateAssessment(
        oasysOffenderPk,
        oasysAssessmentType,
        oasysSetPk,
        any()
      )
    } returns UpdateAssessmentAnswersResponseDto()
    every { questionService.getAllSectionQuestionsForQuestions(any()) } returns QuestionSchemaEntities(questionsList = emptyList())

    val update = mapOf(question1Uuid to makeAnswersDto("YES"))

    oasysAssessmentUpdateService.updateOASysAssessment(setupEpisode(), update)

    verify(exactly = 1) {
      assessmentUpdateRestClient.updateAssessment(
        oasysOffenderPk,
        oasysAssessmentType,
        oasysSetPk,
        any()
      )
    }
  }

  @Test
  fun `don't update OASys if no OASysSet stored against episode`() {
    every { questionService.getAllQuestions() } returns setupQuestionCodes()
    every {
      assessmentUpdateRestClient.updateAssessment(
        oasysOffenderPk,
        oasysAssessmentType,
        oasysSetPk,
        any()
      )
    } returns UpdateAssessmentAnswersResponseDto()

    val episode = AssessmentEpisodeEntity(
      oasysSetPk = oasysSetPk,
      createdDate = LocalDateTime.now()
    )
    val update = mapOf(question1Uuid to makeAnswersDto("YES"))

    oasysAssessmentUpdateService.updateOASysAssessment(episode, update)

    verify(exactly = 0) {
      assessmentUpdateRestClient.updateAssessment(
        oasysOffenderPk,
        oasysAssessmentType,
        oasysSetPk,
        any()
      )
    }
  }

  fun makeAnswersDto(vararg ans: String): AnswersDto {
    return AnswersDto(listOf(AnswerDto(listOf(*ans))))
  }

  private fun setupEpisode(): AssessmentEpisodeEntity {
    val answers = mutableMapOf(
      question1Uuid to AnswerEntity.from("some free text"),
      question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
      question3Uuid to AnswerEntity.from("not mapped to oasys")
    )
    return AssessmentEpisodeEntity(
      episodeId = episodeId1,
      assessmentSchemaCode = AssessmentSchemaCode.ROSH,
      oasysSetPk = oasysSetPk,
      answers = answers,
      createdDate = LocalDateTime.now(),
      assessment = AssessmentEntity(
        subject_ = mutableListOf(
          SubjectEntity(
            oasysOffenderPk = 1,
            subjectUuid = UUID.randomUUID()
          )
        )
      )
    )
  }

  private fun setupQuestionCodes(): QuestionSchemaEntities {
    val dummy = AnswerSchemaGroupEntity(answerSchemaId = 99)

    val yes =
      AnswerSchemaEntity(answerSchemaId = 1, answerSchemaUuid = answer1Uuid, value = "YES", answerSchemaGroup = dummy)
    val maybe =
      AnswerSchemaEntity(answerSchemaId = 2, answerSchemaUuid = answer2Uuid, value = "MAYBE", answerSchemaGroup = dummy)
    val no =
      AnswerSchemaEntity(answerSchemaId = 3, answerSchemaUuid = answer3Uuid, value = "NO", answerSchemaGroup = dummy)

    val group1 = AnswerSchemaGroupEntity(answerSchemaId = 1, answerSchemaEntities = listOf(yes))
    val group2 = AnswerSchemaGroupEntity(answerSchemaId = 2, answerSchemaEntities = listOf(maybe, no))

    return QuestionSchemaEntities(
      listOf(
        makeQuestion(1, question1Uuid, "Q1", "checkbox", group1),
        makeQuestion(2, question2Uuid, "Q2", "radio", group2),
        makeQuestion(3, question3Uuid, "Q3")
      )
    )
  }

  private fun makeQuestion(
    questionSchemaId: Long,
    questionSchemaUuid: UUID,
    questionCode: String,
    answerType: String = "freetext",
    answerSchemaGroup: AnswerSchemaGroupEntity? = null,
    oasysSectionCode: String? = null,
    oasysLogicalPage: Long? = null,
    oasysQuestionCode: String? = null,
  ): QuestionSchemaEntity {
    val oaSysMappings = mutableListOf<OASysMappingEntity>()
    val question = QuestionSchemaEntity(
      questionSchemaId = questionSchemaId,
      questionSchemaUuid = questionSchemaUuid,
      questionCode = questionCode,
      answerType = answerType,
      answerSchemaGroup = answerSchemaGroup,
      oasysMappings = oaSysMappings
    )
    if (oasysSectionCode != null)
      oaSysMappings.add(
        OASysMappingEntity(
          mappingId = questionSchemaId,
          sectionCode = oasysSectionCode,
          logicalPage = oasysLogicalPage,
          questionCode = oasysQuestionCode!!,
          questionSchema = question
        )
      )
    return question
  }

}
