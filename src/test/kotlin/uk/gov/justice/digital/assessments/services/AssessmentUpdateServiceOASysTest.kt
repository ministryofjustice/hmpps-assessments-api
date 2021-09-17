package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Answers
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerSchemaGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.ValidationErrorDto
import uk.gov.justice.digital.assessments.services.dto.AssessmentEpisodeUpdateErrors
import uk.gov.justice.digital.assessments.services.dto.OasysAnswer
import uk.gov.justice.digital.assessments.services.dto.OasysAnswers
import uk.gov.justice.digital.assessments.testutils.Verify
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service OASys Tests")
class AssessmentUpdateServiceOASysTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()
  private val riskPredictorsService: RiskPredictorsService = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService = mockk()
  private val assessmentService: AssessmentService = mockk()

  private val assessmentsUpdateService = AssessmentUpdateService(
    assessmentRepository,
    episodeRepository,
    questionService,
    riskPredictorsService,
    oasysAssessmentUpdateService,
    assessmentService,
  )

  private val assessmentId = 1L
  private val oasysAssessmentType = OasysAssessmentType.SHORT_FORM_PSR
  private val oasysSetPk = 1L

  private val episodeId1 = 1L
  private val episodeId2 = 2L

  private val episodeUuid = UUID.randomUUID()

  private val existingQuestionCode = "existing_question_code"

  private val questionCode1 = "question_code_1"
  private val questionCode2 = "question_code_2"
  private val questionCode3 = "question_code_3"
  private val answer1Uuid = UUID.randomUUID()
  private val answer2Uuid = UUID.randomUUID()
  private val answer3Uuid = UUID.randomUUID()

  @BeforeEach
  fun setup() {
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.RSR) } returns OasysAssessmentType.SOMETHING_IN_OASYS
  }

  @Test
  fun `map Oasys answer from free text answer`() {
    val mapping = OASysMappingEntity(
      sectionCode = "1",
      questionCode = "R1.3",
      logicalPage = 1,
      fixed_field = false,
      mappingId = 1,
      questionSchema = QuestionSchemaEntity(questionSchemaId = 1, questionCode = "question_code")
    )

    val result = OasysAnswers.mapOasysAnswers(mapping, listOf("Free Text"), "radios")[0]

    assertThat(result.answer).isEqualTo("Free Text")
    assertThat(result.logicalPage).isEqualTo(1)
    assertThat(result.isStatic).isFalse
    assertThat(result.questionCode).isEqualTo("R1.3")
    assertThat(result.sectionCode).isEqualTo("1")
  }

  @Test
  fun `map Oasys answer with correct date format`() {
    val mapping = OASysMappingEntity(
      sectionCode = "1",
      questionCode = "R1.3",
      logicalPage = 1,
      fixed_field = false,
      mappingId = 1,
      questionSchema = QuestionSchemaEntity(questionSchemaId = 1, questionCode = "question_code")
    )

    val result = OasysAnswers.mapOasysAnswers(mapping, listOf("1975-01-20T00:00:00.000Z"), "date")[0]

    assertThat(result.answer).isEqualTo("20/01/1975")
    assertThat(result.logicalPage).isEqualTo(1)
    assertThat(result.isStatic).isFalse
    assertThat(result.questionCode).isEqualTo("R1.3")
    assertThat(result.sectionCode).isEqualTo("1")
  }

  @Test
  fun `map Oasys answers from ARN questions and answers`() {
    val answers = mutableMapOf(
      questionCode1 to listOf("some free text"),
      questionCode2 to listOf("1975-01-20T00:00:00.000Z"),
      questionCode3 to listOf("not mapped to oasys"),
    )
    val episode = AssessmentEpisodeEntity(
      answers = answers,
      createdDate = LocalDateTime.now(),
      assessmentSchemaCode = AssessmentSchemaCode.ROSH
    )
    val questions = QuestionSchemaEntities(
      listOf(
        makeQuestion(1, questionCode1, "freetext", null, "section1", 1, "name"),
        makeQuestion(2, questionCode2, "date", null, "section1", 1, "dob"),
        makeQuestion(3, questionCode3)
      )
    )

    val oasysAnswers = OasysAnswers.from(
      episode,
      object : OasysAnswers.Companion.MappingProvider {
        override fun getAllQuestions(): QuestionSchemaEntities = questions
        override fun getTableQuestions(tableCode: String): QuestionSchemaEntities {
          throw RuntimeException("Should not be called")
        }
      }
    )

    assertThat(oasysAnswers).hasSize(2)
    assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "name", "some free text"))
    assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "dob", "20/01/1975"))
  }

  @Nested
  @DisplayName("map Oasys answers from ARN questions and answers with children")
  inner class WithChildren {
    private val tableQuestion = "table_question"
    private val childQuestion1 = "child_question_one"
    private val childQuestion2 = "child_question_two"

    private val childNameQuestion =
      makeQuestion(10, childQuestion1, "freetext", null, "children", null, "childname")
    private val childAddressQuestion =
      makeQuestion(11, childQuestion2, "freetext", null, "children", null, "childaddress")

    val allQuestions = QuestionSchemaEntities(
      listOf(
        makeQuestion(1, questionCode1, "freetext", null, "section1", 1, "name"),
        makeQuestion(2, questionCode2, "date", null, "section1", 1, "dob"),
        makeQuestion(3, questionCode3),
        makeQuestion(4, tableQuestion, "table:children_at_risk"),
        childNameQuestion,
        childAddressQuestion
      )
    )
    val childTableQuestions = QuestionSchemaEntities(
      listOf(
        childNameQuestion,
        childAddressQuestion
      )
    )

    private val testMapper = object : OasysAnswers.Companion.MappingProvider {
      override fun getAllQuestions(): QuestionSchemaEntities = allQuestions
      override fun getTableQuestions(tableCode: String): QuestionSchemaEntities =
        if (tableCode == "children_at_risk")
          childTableQuestions
        else
          throw RuntimeException("Should only be called for children_at_risk table")
    }

    @Test
    fun `with one child`() {
      val answers = mutableMapOf(
        questionCode1 to listOf("some free text"),
        questionCode2 to listOf("1975-01-20T00:00:00.000Z"),
        questionCode3 to listOf("not mapped to oasys"),
      )

      val tables = mutableMapOf(
        "children_at_risk" to mutableListOf(
          mapOf(
            childQuestion1 to listOf("child name"),
            childQuestion2 to listOf("child address"),
          )
        )
      )

      val episode = AssessmentEpisodeEntity(
        answers = answers,
        createdDate = LocalDateTime.now(),
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        tables = tables,
      )
      val oasysAnswers = OasysAnswers.from(episode, testMapper)

      assertThat(oasysAnswers).hasSize(4)
      assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "name", "some free text"))
      assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "dob", "20/01/1975"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 0, "childname", "child name"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 0, "childaddress", "child address"))
    }

    @Test
    fun `with multiple children`() {
      val answers = mutableMapOf(
        questionCode1 to listOf("some free text"),
        questionCode2 to listOf("1975-01-20T00:00:00.000Z"),
        questionCode3 to listOf("not mapped to oasys"),
      )

      val tables = mutableMapOf(
        "children_at_risk" to mutableListOf(
          mapOf(
            childQuestion1 to listOf("name1"),
            childQuestion2 to listOf("address1"),
          ),
          mapOf(
            childQuestion1 to listOf("name2"),
          ),
          mapOf(
            childQuestion1 to listOf("name3"),
            childQuestion2 to listOf("address3"),
          )
        )
      )

      val episode = AssessmentEpisodeEntity(
        answers = answers,
        createdDate = LocalDateTime.now(),
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        tables = tables,
      )

      val oasysAnswers = OasysAnswers.from(episode, testMapper)

      assertThat(oasysAnswers).hasSize(7)
      assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "name", "some free text"))
      assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "dob", "20/01/1975"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 0, "childname", "name1"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 0, "childaddress", "address1"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 1, "childname", "name2"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 2, "childname", "name3"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 2, "childaddress", "address3"))
    }

    @Test
    fun `with multiple children with multi-value answer`() {
      val answers = mutableMapOf(
        questionCode1 to listOf("some free text"),
        questionCode2 to listOf("1975-01-20T00:00:00.000Z"),
        questionCode3 to listOf("not mapped to oasys"),
      )

      val tables = mutableMapOf(
        "children_at_risk" to mutableListOf(
          mapOf(
            childQuestion1 to listOf("name1"),
            childQuestion2 to listOf("address1"),
          ),
          mapOf(
            childQuestion1 to listOf("name2"),
            childQuestion2 to listOf("address2a", "address2b"),
          ),
          mapOf(
            childQuestion1 to listOf("name3"),
            childQuestion2 to listOf("address3"),
          )
        )
      )

      val episode = AssessmentEpisodeEntity(
        answers = answers,
        createdDate = LocalDateTime.now(),
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        tables = tables,
      )

      val oasysAnswers = OasysAnswers.from(episode, testMapper)

      assertThat(oasysAnswers).hasSize(9)
      assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "name", "some free text"))
      assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "dob", "20/01/1975"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 0, "childname", "name1"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 0, "childaddress", "address1"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 1, "childname", "name2"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 1, "childaddress", "address2a"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 1, "childaddress", "address2b"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 2, "childname", "name3"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 2, "childaddress", "address3"))
    }
  }

  @Test
  fun `returns validation errors from OASys`() {
    val question = makeQuestion(9, existingQuestionCode, "freetext", null, "section1", null, "Q1")
    every { questionService.getAllQuestions() } returns QuestionSchemaEntities(listOf(question))

    val assessment = assessmentEntityWithOasysOffender(
      mutableMapOf(
        existingQuestionCode to listOf("free text", "fruit loops", "biscuits")
      )
    )

    val assessmentEpisode = AssessmentEpisodeEntity(
      episodeId = episodeId1,
      assessmentSchemaCode = AssessmentSchemaCode.ROSH,
      createdDate = LocalDateTime.now(),
      oasysSetPk = oasysSetPk,
      assessment = assessment
    )

    every {
      oasysAssessmentUpdateService.updateOASysAssessment(
        assessmentEpisode,
        any()
      )
    } returns AssessmentEpisodeUpdateErrors(
      answerErrors = mutableMapOf(existingQuestionCode to mutableListOf("NO"))
    )
    every { assessmentRepository.save(any()) } returns null // should save when errors?
    every { questionService.getAllSectionQuestionsForQuestions(any()) } returns QuestionSchemaEntities(questionsList = emptyList())

    val oasysError = UpdateAssessmentAnswersResponseDto(
      7777,
      setOf(
        ValidationErrorDto(
          "section1",
          null,
          "Q1",
          "OOPS",
          "NO",
          false
        )
      )
    )
    every {
      assessmentUpdateRestClient.updateAssessment(
        any(),
        oasysAssessmentType,
        oasysSetPk,
        any()
      )
    } returns oasysError
    // Christ, what a lot of set up

    // Apply the update
    val updatedAnswers = UpdateAssessmentEpisodeDto(
      mapOf(existingQuestionCode to listOf("fruit loops", "custard"))
    )
    val episodeDto = assessmentsUpdateService.updateEpisode(assessmentEpisode, updatedAnswers)

    // Updated answers in returned DTO
    assertThat(episodeDto.answers).hasSize(1)
    Verify.multiAnswers(
      episodeDto.answers[existingQuestionCode]!!,
      "fruit loops",
      "custard"
    )

    // But also errors!
    assertThat(episodeDto.errors).hasSize(1)
    with(episodeDto.errors!![existingQuestionCode]!!) {
      assertThat(size).isEqualTo(1)
      assertThat(this).contains("NO")
    }
  }

  @Test
  fun `update episode calls updateOASysAssessment with the updated answers`() {
    val assessmentEpisode = setupEpisode()

    every { questionService.getAllSectionQuestionsForQuestions(listOf(questionCode1)) } returns setupSectionQuestionCodes()

    val update = UpdateAssessmentEpisodeDto(answers = mapOf(questionCode1 to listOf("Updated")))
    every {
      oasysAssessmentUpdateService.updateOASysAssessment(assessmentEpisode, update.answers)
    } returns AssessmentEpisodeUpdateErrors()
    every { questionService.getAllQuestions() } returns setupQuestionCodes()
    every { assessmentRepository.save(any()) } returns mockk()

    val updatedEpisode = assessmentsUpdateService.updateEpisode(assessmentEpisode, update)

    verify(exactly = 1) {
      oasysAssessmentUpdateService.updateOASysAssessment(
        assessmentEpisode,
        update.answers,
      )
    }

    with(updatedEpisode.answers) {
      assertThat(map { it.key }).containsOnlyOnce(questionCode1, questionCode2)
      assertThat(map { it.value }).contains(
        listOf("Updated"),
        listOf("1975-01-20T00:00:00.000Z"),
        listOf("not mapped to oasys"),
      )
    }
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
        makeQuestion(1, "Q1", "checkbox", group1),
        makeQuestion(2, "Q2", "radio", group2),
        makeQuestion(3, "Q3")
      )
    )
  }

  private fun setupSectionQuestionCodes(): QuestionSchemaEntities {
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
        makeQuestion(1, "Q1", "checkbox", group1, "section1", 1, "oasysQ1"),
        makeQuestion(2, "Q2", "radio", group2, "section1", 1, "oasysQ2")
      )
    )
  }

  private fun makeQuestion(
    questionSchemaId: Long,
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
      questionSchemaUuid = UUID.randomUUID(),
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

  private fun assessmentEntityWithOasysOffender(answers: Answers): AssessmentEntity {
    val subject = SubjectEntity(
      oasysOffenderPk = 9999,
      dateOfBirth = LocalDate.of(1989, 1, 1),
      crn = "X1345",
      source = "DELIUS",
      sourceId = "128647"
    )
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
        createdDate = LocalDateTime.now(),
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        changeReason = "Change of Circs 2",
        oasysSetPk = 7777,
        answers = answers
      )
    )

    return assessment
  }

  private fun setupEpisode(): AssessmentEpisodeEntity {
    val answers = mutableMapOf(
      questionCode1 to listOf("some free text"),
      questionCode2 to listOf("1975-01-20T00:00:00.000Z"),
      questionCode3 to listOf("not mapped to oasys"),
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
            subjectUuid = UUID.randomUUID(),
            dateOfBirth = LocalDate.of(1989, 1, 1),
            crn = "X1345",
            source = "DELIUS",
            sourceId = "128647"
          )
        )
      )
    )
  }
}
