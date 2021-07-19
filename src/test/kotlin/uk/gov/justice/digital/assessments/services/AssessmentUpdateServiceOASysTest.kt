package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.AnswerDto
import uk.gov.justice.digital.assessments.api.AnswersDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.Answer
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.OASysMappingEntity
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.EpisodeRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.ValidationErrorDto
import uk.gov.justice.digital.assessments.services.dto.OasysAnswers
import uk.gov.justice.digital.assessments.testutils.Verify
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service OASys Tests")
class AssessmentUpdateServiceOASysTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()
  private val predictorService: PredictorService = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()

  private val assessmentsUpdateService = AssessmentUpdateService(
    assessmentRepository,
    episodeRepository,
    questionService,
    assessmentUpdateRestClient,
    predictorService,
    assessmentSchemaService
  )

  private val assessmentUuid = UUID.randomUUID()
  private val assessmentId = 1L
  private val oasysAssessmentType = OasysAssessmentType.SHORT_FORM_PSR
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
      questionSchema = QuestionSchemaEntity(questionSchemaId = 1)
    )

    val result = OasysAnswers.mapOasysAnswers(mapping, listOf(Answer("Free Text")), "radios")[0]

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
      questionSchema = QuestionSchemaEntity(questionSchemaId = 1)
    )

    val result = OasysAnswers.mapOasysAnswers(mapping, listOf(Answer("1975-01-20T00:00:00.000Z")), "date")[0]

    assertThat(result.answer).isEqualTo("20/01/1975")
    assertThat(result.logicalPage).isEqualTo(1)
    assertThat(result.isStatic).isFalse
    assertThat(result.questionCode).isEqualTo("R1.3")
    assertThat(result.sectionCode).isEqualTo("1")
  }

  @Test
  fun `map Oasys answers from ARN questions and answers`() {
    val answers = mutableMapOf(
      question1Uuid to AnswerEntity.from("some free text"),
      question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
      question3Uuid to AnswerEntity.from("not mapped to oasys")
    )
    val episode = AssessmentEpisodeEntity(
      answers = answers
    )
    val questions = QuestionSchemaEntities(
      listOf(
        makeQuestion(1, question1Uuid, "FreeText", "freetext", null, "section1", 1, "name"),
        makeQuestion(2, question2Uuid, "Date", "date", null, "section1", 1, "dob"),
        makeQuestion(3, question3Uuid, "ExtraInfo")
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
    private val tableQuestion = UUID.randomUUID()
    private val childQuestion1 = UUID.randomUUID()
    private val childQuestion2 = UUID.randomUUID()

    private val childNameQuestion =
      makeQuestion(10, childQuestion1, "Name", "freetext", null, "children", null, "childname")
    private val childAddressQuestion =
      makeQuestion(11, childQuestion2, "Address", "freetext", null, "children", null, "childaddress")

    val allQuestions = QuestionSchemaEntities(
      listOf(
        makeQuestion(1, question1Uuid, "FreeText", "freetext", null, "section1", 1, "name"),
        makeQuestion(2, question2Uuid, "Date", "date", null, "section1", 1, "dob"),
        makeQuestion(3, question3Uuid, "ExtraInfo"),
        makeQuestion(4, tableQuestion, "Children", "table:children_at_risk"),
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
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from("child name"),
        childQuestion2 to AnswerEntity.from("child address")
      )

      val episode = AssessmentEpisodeEntity(
        answers = answers
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
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from(listOf("name1", "name2", "name3")),
        childQuestion2 to AnswerEntity.from(listOf("address1", "", "address3"))
      )

      val episode = AssessmentEpisodeEntity(
        answers = answers
      )

      val oasysAnswers = OasysAnswers.from(episode, testMapper)

      assertThat(oasysAnswers).hasSize(8)
      assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "name", "some free text"))
      assertThat(oasysAnswers).contains(OasysAnswer("section1", 1, "dob", "20/01/1975"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 0, "childname", "name1"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 0, "childaddress", "address1"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 1, "childname", "name2"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 1, "childaddress", ""))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 2, "childname", "name3"))
      assertThat(oasysAnswers).contains(OasysAnswer("children", 2, "childaddress", "address3"))
    }

    @Test
    fun `with multiple children with multi-value answer`() {
      val answers = mutableMapOf(
        question1Uuid to AnswerEntity.from("some free text"),
        question2Uuid to AnswerEntity.from("1975-01-20T00:00:00.000Z"),
        question3Uuid to AnswerEntity.from("not mapped to oasys"),
        childQuestion1 to AnswerEntity.from(listOf("name1", "name2", "name3")),
        childQuestion2 to AnswerEntity(
          listOf(
            Answer(listOf("address1")),
            Answer(listOf("address2a", "address2b")),
            Answer(listOf("address3"))
          )
        )
      )

      val episode = AssessmentEpisodeEntity(
        answers = answers
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

    assessmentsUpdateService.updateOASysAssessment(setupEpisode(), update)

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
      oasysSetPk = oasysSetPk
    )
    val update = mapOf(question1Uuid to makeAnswersDto("YES"))

    assessmentsUpdateService.updateOASysAssessment(episode, update)

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
  fun `returns validation errors from OASys`() {
    val question = makeQuestion(9, existingQuestionUuid, "question", "freetext", null, "section1", null, "Q1")
    every { questionService.getAllQuestions() } returns QuestionSchemaEntities(listOf(question))

    val assessment = assessmentEntityWithOasysOffender(
      mutableMapOf(
        existingQuestionUuid to AnswerEntity.from(listOf("free text", "fruit loops", "biscuits"))
      )
    )

    val assessmentEpisode = AssessmentEpisodeEntity(
      episodeId = episodeId1,
      assessmentSchemaCode = AssessmentSchemaCode.ROSH,
      oasysSetPk = oasysSetPk,
      assessment = assessment
    )

    every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
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
    every { predictorService.getPredictorResults(AssessmentSchemaCode.ROSH, assessmentEpisode) } returns emptyList()
    // Christ, what a lot of set up

    // Apply the update
    val updatedAnswers = UpdateAssessmentEpisodeDto(
      mapOf(existingQuestionUuid to listOf("fruit loops", "custard"))
    )
    val episodeDto = assessmentsUpdateService.updateEpisode(assessmentEpisode, updatedAnswers)

    // Updated answers in returned DTO
    assertThat(episodeDto.answers).hasSize(1)
    Verify.singleAnswer(
      episodeDto.answers[existingQuestionUuid]!!,
      "fruit loops",
      "custard"
    )

    // But also errors!
    assertThat(episodeDto.errors).hasSize(1)
    with(episodeDto.errors!![existingQuestionUuid]!!) {
      assertThat(size).isEqualTo(1)
      assertThat(this).contains("NO")
    }
  }

  @Test
  fun `update episode sends only updated sections to oasys`() {
    val assessmentEpisode = setupEpisode()

    every { questionService.getAllSectionQuestionsForQuestions(listOf(question1Uuid)) } returns setupSectionQuestionCodes()

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
    every { assessmentRepository.save(any()) } returns mockk()
    every { predictorService.getPredictorResults(AssessmentSchemaCode.ROSH, assessmentEpisode) } returns emptyList()

    val update = UpdateAssessmentEpisodeDto(answers = mapOf(question1Uuid to listOf("Updated")))
    val updatedEpisode = assessmentsUpdateService.updateEpisode(assessmentEpisode, update)

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
      assertThat(first { it.questionCode == "oasysQ1" }.answer).isEqualTo("Updated")
      assertThat(first { it.questionCode == "oasysQ2" }.answer).isEqualTo("1975-01-20T00:00:00.000Z")
      assertThat(map { it.answer }).doesNotContain("not mapped to oasys")
    }

    with(updatedEpisode.answers) {
      assertThat(map { it.key }).containsOnlyOnce(question1Uuid, question2Uuid)
      assertThat(flatMap { it.value.answers }).contains(
        AnswerDto(listOf("Updated")),
        AnswerDto(listOf("1975-01-20T00:00:00.000Z")),
        AnswerDto(listOf("not mapped to oasys"))
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
        makeQuestion(1, question1Uuid, "Q1", "checkbox", group1),
        makeQuestion(2, question2Uuid, "Q2", "radio", group2),
        makeQuestion(3, question3Uuid, "Q3")
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
        makeQuestion(1, question1Uuid, "Q1", "checkbox", group1, "section1", 1, "oasysQ1"),
        makeQuestion(2, question2Uuid, "Q2", "radio", group2, "section1", 1, "oasysQ2")
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
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        changeReason = "Change of Circs 2",
        oasysSetPk = 7777,
        answers = answers
      )
    )

    return assessment
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
}
