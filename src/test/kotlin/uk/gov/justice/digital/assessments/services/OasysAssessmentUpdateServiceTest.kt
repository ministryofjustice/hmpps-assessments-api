package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.services.dto.AssessmentEpisodeUpdateErrors
import uk.gov.justice.digital.assessments.services.dto.OasysAnswer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Oasys Assessment Update Service Tests")
class OasysAssessmentUpdateServiceTest() {
  private val questionService: QuestionService = mockk()
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()
  private val assessmentReferenceDataService: AssessmentReferenceDataService = mockk()

  private val oasysAssessmentUpdateService = OasysAssessmentUpdateService(
    questionService,
    assessmentUpdateRestClient,
    assessmentReferenceDataService
  )

  private val questionCode1 = "Q1"
  private val questionCode2 = "Q2"
  private val questionCode3 = "Q3"
  private val answer1Uuid = UUID.randomUUID()
  private val answer2Uuid = UUID.randomUUID()
  private val answer3Uuid = UUID.randomUUID()
  private val oasysOffenderPk = 1L
  private val oasysSetPk = 1L
  private val episodeId1 = 1L
  private val oasysAssessmentType = OasysAssessmentType.SHORT_FORM_PSR

  @BeforeEach
  fun setup() {
    every { assessmentReferenceDataService.toOasysAssessmentType(AssessmentType.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
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

    val update = mapOf(questionCode1 to listOf("YES"))

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
      createdDate = LocalDateTime.now(),
      assessmentType = AssessmentType.ROSH,
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      assessment = AssessmentEntity()
    )
    val update = mapOf(questionCode1 to listOf("YES"))

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

  @Test
  fun `update episode sends only updated sections to oasys`() {
    val assessmentEpisode = setupEpisode()

    every { questionService.getAllSectionQuestionsForQuestions(listOf(questionCode1)) } returns setupSectionQuestionCodes()

    val update = UpdateAssessmentEpisodeDto(answers = mapOf(questionCode1 to listOf("Updated")))
    val oasysAnswersSlot = slot<Set<OasysAnswer>>()
    every {
      assessmentUpdateRestClient.updateAssessment(
        oasysOffenderPk,
        oasysAssessmentType,
        oasysSetPk,
        capture(oasysAnswersSlot)
      )
    } returns UpdateAssessmentAnswersResponseDto()
    every { questionService.getAllQuestions() } returns setupQuestionCodes()

    val updateErrors = oasysAssessmentUpdateService.updateOASysAssessment(assessmentEpisode, update.answers)

    verify(exactly = 1) {
      assessmentUpdateRestClient.updateAssessment(
        oasysOffenderPk,
        oasysAssessmentType,
        oasysSetPk,
        any()
      )
    }
    with(oasysAnswersSlot.captured) {
      assertThat(map { it.questionCode }).containsOnly("oasysQ1", "oasysQ2")
      assertThat(map { it.sectionCode }).containsOnly("section1")
      assertThat(first { it.questionCode == "oasysQ1" }.answer).isEqualTo("some free text")
      assertThat(first { it.questionCode == "oasysQ2" }.answer).isEqualTo("1975-01-20T00:00:00.000Z")
      assertThat(map { it.answer }).doesNotContain("not mapped to oasys")
    }
    assertFalse(updateErrors.hasErrors())
  }

  @Test
  fun `Update oasys assessment returns errors when offender null`() {
    val assessmentEpisode = AssessmentEpisodeEntity(
      episodeId = 128,
      assessmentType = AssessmentType.ROSH,
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      assessment = AssessmentEntity()
    )
    val update = UpdateAssessmentEpisodeDto(answers = mapOf(questionCode1 to listOf("Updated")))
    val updateAssessmentResponse =
      oasysAssessmentUpdateService.updateOASysAssessment(assessmentEpisode, update.answers)

    assertThat(updateAssessmentResponse).isEqualTo(AssessmentEpisodeUpdateErrors(errorsInAssessment = mutableListOf("Unable to update OASys Assessment with keys type: ROSH oasysSet: null offenderPk: null, values cant be null")))
  }

  @Test
  fun `Complete oasys assessment returns errors when offender null`() {
    val assessmentEpisode = AssessmentEpisodeEntity(
      episodeId = 128,
      assessmentType = AssessmentType.ROSH,
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      assessment = AssessmentEntity()
    )
    val completeAssessmentResponse =
      oasysAssessmentUpdateService.completeOASysAssessment(assessmentEpisode, null)

    assertThat(completeAssessmentResponse).isEqualTo(AssessmentEpisodeUpdateErrors(errorsInAssessment = mutableListOf("Unable to complete OASys Assessment with keys type: ROSH oasysSet: null offenderPk: null, values cant be null")))
  }

  private fun setupSectionQuestionCodes(): QuestionSchemaEntities {
    val dummy = AnswerGroupEntity(answerGroupId = 99)

    val yes =
      AnswerEntity(answerId = 1, answerUuid = answer1Uuid, value = "YES", answerGroup = dummy)
    val maybe =
      AnswerEntity(answerId = 2, answerUuid = answer2Uuid, value = "MAYBE", answerGroup = dummy)
    val no =
      AnswerEntity(answerId = 3, answerUuid = answer3Uuid, value = "NO", answerGroup = dummy)

    val group1 = AnswerGroupEntity(answerGroupId = 1, answerEntities = listOf(yes))
    val group2 = AnswerGroupEntity(answerGroupId = 2, answerEntities = listOf(maybe, no))

    return QuestionSchemaEntities(
      listOf(
        makeQuestion(
          questionSchemaId = 1,
          questionCode = questionCode1,
          answerType = "checkbox",
          answerSchemaGroup = group1,
          oasysSectionCode = "section1",
          oasysLogicalPage = 1,
          oasysQuestionCode = "oasysQ1"
        ),
        makeQuestion(
          questionSchemaId = 2,
          questionCode = questionCode2,
          answerType = "radio",
          answerSchemaGroup = group2,
          oasysSectionCode = "section1",
          oasysLogicalPage = 1,
          oasysQuestionCode = "oasysQ2"
        )
      )
    )
  }

  private fun setupEpisode(): AssessmentEpisodeEntity {
    val answers = mutableMapOf<String, List<Any>>(
      questionCode1 to listOf("some free text"),
      questionCode2 to listOf("1975-01-20T00:00:00.000Z"),
      questionCode3 to listOf("not mapped to oasys")
    )
    return AssessmentEpisodeEntity(
      episodeId = episodeId1,
      assessmentType = AssessmentType.ROSH,
      oasysSetPk = oasysSetPk,
      answers = answers,
      createdDate = LocalDateTime.now(),
      assessment = AssessmentEntity(
        subject =
        SubjectEntity(
          oasysOffenderPk = 1,
          subjectUuid = UUID.randomUUID(),
          dateOfBirth = LocalDate.of(1989, 1, 1),
          crn = "X1345",
        )
      ),
      author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
    )
  }

  private fun setupQuestionCodes(): QuestionSchemaEntities {
    val dummy = AnswerGroupEntity(answerGroupId = 99)

    val yes =
      AnswerEntity(answerId = 1, answerUuid = answer1Uuid, value = "YES", answerGroup = dummy)
    val maybe =
      AnswerEntity(answerId = 2, answerUuid = answer2Uuid, value = "MAYBE", answerGroup = dummy)
    val no =
      AnswerEntity(answerId = 3, answerUuid = answer3Uuid, value = "NO", answerGroup = dummy)

    val group1 = AnswerGroupEntity(answerGroupId = 1, answerEntities = listOf(yes))
    val group2 = AnswerGroupEntity(answerGroupId = 2, answerEntities = listOf(maybe, no))

    return QuestionSchemaEntities(
      listOf(
        makeQuestion(1, questionCode1, "checkbox", group1),
        makeQuestion(2, questionCode2, "radio", group2),
        makeQuestion(3, questionCode3)
      )
    )
  }

  private fun makeQuestion(
    questionSchemaId: Long,
    questionCode: String,
    answerType: String = "freetext",
    answerSchemaGroup: AnswerGroupEntity? = null,
    oasysSectionCode: String? = null,
    oasysLogicalPage: Long? = null,
    oasysQuestionCode: String? = null,
  ): QuestionEntity {
    val oaSysMappings = mutableListOf<OASysMappingEntity>()
    val question = QuestionEntity(
      questionId = questionSchemaId,
      questionUuid = UUID.randomUUID(),
      questionCode = questionCode,
      answerType = answerType,
      answerGroup = answerSchemaGroup,
      oasysMappings = oaSysMappings
    )
    if (oasysSectionCode != null)
      oaSysMappings.add(
        OASysMappingEntity(
          mappingId = questionSchemaId,
          sectionCode = oasysSectionCode,
          logicalPage = oasysLogicalPage,
          questionCode = oasysQuestionCode!!,
          question = question
        )
      )
    return question
  }
}
