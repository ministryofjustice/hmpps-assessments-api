package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.EpisodeRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.ValidationErrorDto
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.services.dto.OasysAnswers
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service OASys Tests")
class AssessmentServiceOASysTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    episodeRepository,
    subjectRepository,
    questionService,
    episodeService,
    courtCaseRestClient,
    assessmentUpdateRestClient,
    offenderService
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val assessmentType = AssessmentType.SHORT_FORM_PSR

  private val oasysOffenderPk = 1L
  private val oasysSetPk = 1L

  private val episodeId1 = 1L
  private val episodeId2 = 2L

  private val episodeUuid = UUID.randomUUID()

  private val existingQuestionUuid = UUID.randomUUID()

  private val question1Uuid = UUID.randomUUID()
  private val question2Uuid = UUID.randomUUID()
  private val question3Uuid = UUID.randomUUID()
  private val answer1Uuid = UUID.randomUUID()
  private val answer2Uuid = UUID.randomUUID()
  private val answer3Uuid = UUID.randomUUID()

  @Test
  fun `map Oasys answer from free text answer`() {
    val mapping = OASysMappingEntity(sectionCode = "1", questionCode = "R1.3", logicalPage = 1, fixed_field = false, mappingId = 1, questionSchema = QuestionSchemaEntity(questionSchemaId = 1))

    val result = OasysAnswers.mapOasysAnswer(mapping, listOf("Free Text"), "radios")[0]

    assertThat(result.answer).isEqualTo("Free Text")
    assertThat(result.logicalPage).isEqualTo(1)
    assertThat(result.isStatic).isFalse()
    assertThat(result.questionCode).isEqualTo("R1.3")
    assertThat(result.sectionCode).isEqualTo("1")
  }

  @Test
  fun `map Oasys answer with correct date format`() {
    val mapping = OASysMappingEntity(sectionCode = "1", questionCode = "R1.3", logicalPage = 1, fixed_field = false, mappingId = 1, questionSchema = QuestionSchemaEntity(questionSchemaId = 1))

    val result = OasysAnswers.mapOasysAnswer(mapping, listOf("1975-01-20T00:00:00.000Z"), "date")[0]

    assertThat(result.answer).isEqualTo("20/01/1975")
    assertThat(result.logicalPage).isEqualTo(1)
    assertThat(result.isStatic).isFalse()
    assertThat(result.questionCode).isEqualTo("R1.3")
    assertThat(result.sectionCode).isEqualTo("1")
  }

  @Test
  fun `map Oasys answers from ARN questions and answers`() {
    val answers = mutableMapOf(
      question1Uuid to AnswerEntity("some free text"),
      question2Uuid to AnswerEntity("1975-01-20T00:00:00.000Z"),
      question3Uuid to AnswerEntity("not mapped to oasys")
    )
    val episode = AssessmentEpisodeEntity(
      answers = answers
    )
    val questions = QuestionSchemaEntities(listOf(
      makeQuestion(1, question1Uuid, "FreeText", "freetext", null, "section1", 1, "name"),
      makeQuestion(2, question2Uuid, "Date", "date", null, "section1", 1, "dob"),
      makeQuestion(3, question3Uuid, "ExtraInfo")
    ))

    val oasysAnswers = OasysAnswers.from(episode, object: OasysAnswers.Companion.MappingProvider  {
      override fun getAllQuestions(): QuestionSchemaEntities = questions
    })

    assertThat(oasysAnswers).hasSize(2)
    assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "name", "some free text"))
    assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "dob", "20/01/1975"))
  }

  @Test
  fun `map Oasys answers from ARN questions and answers with one child`() {
    val tableQuestion = UUID.randomUUID()
    val childQuestion1 = UUID.randomUUID()
    val childQuestion2 = UUID.randomUUID()
    val answers = mutableMapOf(
      question1Uuid to AnswerEntity("some free text"),
      question2Uuid to AnswerEntity("1975-01-20T00:00:00.000Z"),
      question3Uuid to AnswerEntity("not mapped to oasys"),
      childQuestion1 to AnswerEntity("child name"),
      childQuestion2 to AnswerEntity("child address")
    )

    val episode = AssessmentEpisodeEntity(
      answers = answers
    )
    val questions = QuestionSchemaEntities(listOf(
      makeQuestion(1, question1Uuid, "FreeText", "freetext", null, "section1", 1, "name"),
      makeQuestion(2, question2Uuid, "Date", "date", null, "section1", 1, "dob"),
      makeQuestion(3, question3Uuid, "ExtraInfo"),
      makeQuestion(4, tableQuestion, "Children", "table:children_at_risk"),
      makeQuestion(10, childQuestion1, "Name", "freetext", null, "children", null, "childname"),
      makeQuestion(11, childQuestion2, "Address", "freetext", null, "children", null, "childaddress")
    ))

    val oasysAnswers = OasysAnswers.from(episode, object: OasysAnswers.Companion.MappingProvider  {
      override fun getAllQuestions(): QuestionSchemaEntities = questions
    })

    assertThat(oasysAnswers).hasSize(4)
    assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "name", "some free text"))
    assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "dob", "20/01/1975"))
    assertThat(oasysAnswers).contains(OasysAnswer("children", 1, "childname", "child name"))
    assertThat(oasysAnswers).contains(OasysAnswer("children", 1, "childaddress", "child address"))
  }

  @Test
  fun `update OASys if OASysSet stored against episode`() {
    every { questionService.getAllQuestions() } returns setupQuestionCodes()
    every { assessmentUpdateRestClient.updateAssessment(oasysOffenderPk, oasysSetPk, assessmentType, any()) } returns UpdateAssessmentAnswersResponseDto()

    val episode = AssessmentEpisodeEntity(
      episodeId = episodeId1,
      assessmentType = AssessmentType.SHORT_FORM_PSR,
      oasysSetPk = oasysSetPk
    )

    assessmentsService.updateOASysAssessment(oasysOffenderPk, episode)

    verify(exactly = 1) {
      assessmentUpdateRestClient.updateAssessment(oasysOffenderPk, oasysSetPk, assessmentType, any())
    }
  }

  @Test
  fun `don't update OASys if no OASysSet stored against episode`() {
    every { questionService.getAllQuestions() } returns setupQuestionCodes()
    every { assessmentUpdateRestClient.updateAssessment(oasysOffenderPk, oasysSetPk, assessmentType, any()) } returns UpdateAssessmentAnswersResponseDto()

    val episode = AssessmentEpisodeEntity(
      oasysSetPk = oasysSetPk
    )

    assessmentsService.updateOASysAssessment(oasysOffenderPk, episode)

    verify(exactly = 0) {
      assessmentUpdateRestClient.updateAssessment(oasysOffenderPk, oasysSetPk, assessmentType, any())
    }
  }

  @Test
  fun `returns validation errors from OASys`() {
    val question = makeQuestion(9, existingQuestionUuid, "question", "freetext", null, "section1", null, "Q1")
    every { questionService.getAllQuestions() } returns QuestionSchemaEntities(listOf(question))

    val assessment = assessmentEntityWithOasysOffender(mutableMapOf(
      existingQuestionUuid to AnswerEntity(listOf("free text", "fruit loops", "biscuits"))
    ))
    every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
    every { assessmentRepository.save(any()) } returns null // should save when errors?

    val oasysError = UpdateAssessmentAnswersResponseDto(
      7777,
      setOf(ValidationErrorDto(
        "section1",
        null,
        "Q1",
        "OOPS",
        "NO",
        false))
    )
    every { assessmentUpdateRestClient.updateAssessment(any(), any(), any(), any(), any(), any()) } returns oasysError
    // Christ, what a lot of set up

    // Apply the update
    val updatedAnswers = UpdateAssessmentEpisodeDto(
      mapOf(existingQuestionUuid to listOf("fruit loops", "custard"))
    )
    val episodeDto = assessmentsService.updateEpisode(assessmentUuid, episodeUuid, updatedAnswers)

    // Updated answers in returned DTO
    assertThat(episodeDto.answers).hasSize(1)
    with(episodeDto.answers[existingQuestionUuid]!!) {
      assertThat(size).isEqualTo(2)
      assertThat(this).containsAll(listOf("fruit loops", "custard"))
    }

    // But also errors!
    assertThat(episodeDto.errors).hasSize(1)
    with(episodeDto.errors!![existingQuestionUuid]!!) {
      assertThat(size).isEqualTo(1)
      assertThat(this).contains("NO")
    }
  }

  private fun setupQuestionCodes(): QuestionSchemaEntities {
    val dummy = AnswerSchemaGroupEntity(answerSchemaId = 99)

    val yes = AnswerSchemaEntity(answerSchemaId = 1, answerSchemaUuid = answer1Uuid, value = "YES", answerSchemaGroup = dummy)
    val maybe = AnswerSchemaEntity(answerSchemaId = 2, answerSchemaUuid = answer2Uuid, value = "MAYBE", answerSchemaGroup = dummy)
    val no = AnswerSchemaEntity(answerSchemaId = 3, answerSchemaUuid = answer3Uuid, value = "NO", answerSchemaGroup = dummy)

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

  private fun assessmentEntityWithOasysOffender(answers: MutableMap<UUID, AnswerEntity>): AssessmentEntity {
    val subject = SubjectEntity(oasysOffenderPk = 9999)
    val episodes = mutableListOf<AssessmentEpisodeEntity>()
    val assessment = AssessmentEntity(
      assessmentId = assessmentId,
      episodes = episodes,
      subject_ = mutableListOf(subject)
    )

    episodes.add(
      AssessmentEpisodeEntity(
        episodeUuid = episodeUuid,
        episodeId = episodeId2,
        assessment = assessment,
        assessmentType = AssessmentType.SHORT_FORM_PSR,
        changeReason = "Change of Circs 2",
        oasysSetPk = 7777,
        answers = answers
      )
    )

    return assessment
  }
}
