package uk.gov.justice.digital.assessments.services

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.TableQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode.RSR
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.CloneAssessmentExcludedQuestionsEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.CloneAssessmentExcludedQuestionsRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentApiRestClient
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.dto.ExternalSourceQuestionSchemaDto
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@DisplayName("Episode Service Tests")
class EpisodeServiceTest {

  private val questionService: QuestionService = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val assessmentApiRestClient: AssessmentApiRestClient = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()
  private val cloneAssessmentExcludedQuestionsRepository: CloneAssessmentExcludedQuestionsRepository = mockk()

  private val episodeService = EpisodeService(
    questionService,
    courtCaseRestClient,
    communityApiRestClient,
    assessmentApiRestClient,
    assessmentSchemaService,
    cloneAssessmentExcludedQuestionsRepository
  )

  private lateinit var newEpisode: AssessmentEpisodeEntity

  private val author = AuthorEntity(
    userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"
  )

  companion object {
    private const val crn: String = "someCrn"
  }

  @BeforeEach
  fun setup() {
    newEpisode = AssessmentEpisodeEntity(
      episodeId = 1,
      assessmentSchemaCode = AssessmentSchemaCode.ROSH,
      author = author,
      assessment = AssessmentEntity(
        subject = SubjectEntity(
          crn = crn,
          dateOfBirth = LocalDate.parse("1999-12-31")
        )
      )
    )
    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentSchemaCode(AssessmentSchemaCode.ROSH) } returns emptyList()
  }

  @Test
  fun `prepopuates answers from external source OASys`() {
    val externalSource = "OASYS"

    newEpisode = AssessmentEpisodeEntity(
      episodeId = 1,
      assessmentSchemaCode = RSR,
      author = author,
      assessment = AssessmentEntity(
        subject = SubjectEntity(
          crn = crn,
          dateOfBirth = LocalDate.parse("1999-12-31")
        )
      )
    )

    val questions: List<ExternalSourceQuestionSchemaDto> = listOf(
      ExternalSourceQuestionSchemaDto(
        "question_1", externalSource,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='1.24')].answer[0]",
        "array", "some/endpoint", ifEmpty = false
      ),
      ExternalSourceQuestionSchemaDto(
        "question_2", externalSource,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='1.26')].answer[0]",
        "array", "some/endpoint", ifEmpty = false
      ),
    )

    every { questionService.getAllQuestions().withExternalSource(RSR) } returns questions

    val json = this::class.java.getResource("/json/oasysLatestRSRShortAssessment.json")?.readText()

    every {
      assessmentApiRestClient.getOASysLatestAssessment(
        crn = crn,
        status = listOf("SIGNED", "COMPLETE"),
        types = listOf("LAYER_1", "LAYER_3"),
        cutoffDate = any()
      )
    } returns json

    val result =
      episodeService.prepopulateFromExternalSources(newEpisode, RSR)

    val expectedAnswers = mutableMapOf(
      "question_1" to listOf("answer 1"),
      "question_2" to listOf("answer 2")
    )
    assertThat(result.answers).containsExactlyEntriesOf(expectedAnswers)
  }

  @Test
  fun `does not prepopuate answers from external source as OASys does not return an assessment`() {
    val externalSource = "OASYS"

    newEpisode = AssessmentEpisodeEntity(
      episodeId = 1,
      assessmentSchemaCode = RSR,
      author = author,
      assessment = AssessmentEntity(
        subject = SubjectEntity(
          crn = crn,
          dateOfBirth = LocalDate.parse("1999-12-31")
        )
      )
    )

    val questions: List<ExternalSourceQuestionSchemaDto> = listOf(
      ExternalSourceQuestionSchemaDto(
        "question_1", externalSource,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='1.24')].answer[0]",
        "array", "some/endpoint", ifEmpty = false
      ),
      ExternalSourceQuestionSchemaDto(
        "question_2", externalSource,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='1.26')].answer[0]",
        "array", "some/endpoint", ifEmpty = false
      ),
    )

    val now = LocalDateTime.now()
    val latestEndDate = now.minusWeeks(5)

    val previousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentSchemaCode = RSR,
        author = author,
        assessment = AssessmentEntity(),
        endDate = now,
        answers = mutableMapOf(
          "question_1" to listOf("previous answer 1"),
          "question_2" to listOf("previous answer 2")
        )
      ),
      AssessmentEpisodeEntity(
        episodeId = 3,
        assessmentSchemaCode = RSR,
        author = author,
        assessment = AssessmentEntity(),
        endDate = latestEndDate,
        answers = mutableMapOf(
          "question_3" to listOf("previous answer 3"),
          "question_4" to listOf("previous answer 4")
        )
      )
    )
    newEpisode.assessment.episodes.addAll(previousEpisodes)

    every { questionService.getAllQuestions().withExternalSource(RSR) } returns questions
    every {
      assessmentApiRestClient.getOASysLatestAssessment(
        crn = crn,
        status = listOf("SIGNED", "COMPLETE"),
        types = listOf("LAYER_1", "LAYER_3"),
        cutoffDate = latestEndDate
      )
    } returns null

    val result =
      episodeService.prepopulateFromExternalSources(newEpisode, RSR)

    assertThat(result.answers).isEmpty()
  }

  @Test
  fun `does not prepopuate answers from external source as no matching answers in OASys`() {
    val externalSource = "OASYS"

    newEpisode = AssessmentEpisodeEntity(
      episodeId = 1,
      assessmentSchemaCode = RSR,
      author = author,
      assessment = AssessmentEntity(
        subject = SubjectEntity(
          crn = crn,
          dateOfBirth = LocalDate.parse("1999-12-31")
        )
      )
    )

    val questions: List<ExternalSourceQuestionSchemaDto> = listOf(
      ExternalSourceQuestionSchemaDto(
        "question_1", externalSource,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='XXX')].answer[0]",
        "array", "some/endpoint", ifEmpty = false
      ),
      ExternalSourceQuestionSchemaDto(
        "question_2", externalSource,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='YYY')].answer[0]",
        "array", "some/endpoint", ifEmpty = false
      ),
    )

    every { questionService.getAllQuestions().withExternalSource(RSR) } returns questions

    val json = this::class.java.getResource("/json/oasysLatestRSRShortAssessment.json")?.readText()

    every {
      assessmentApiRestClient.getOASysLatestAssessment(
        crn = crn,
        status = listOf("SIGNED", "COMPLETE"),
        types = listOf("LAYER_1", "LAYER_3"),
        cutoffDate = any()
      )
    } returns json

    val result =
      episodeService.prepopulateFromExternalSources(newEpisode, RSR)

    assertThat(result.answers["question_1"]).isEmpty()
    assertThat(result.answers["question_2"]).isEmpty()
  }

  @Test
  fun `copies answers and tables from previous episode ignoring excluded questions`() {
    val schemaQuestions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
      TableQuestionDto(tableCode = "table_1")
    )

    val mixedPreviousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        author = author,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "question_1" to listOf("answer_1"),
          "question_2" to listOf("answer_2")
        )
      ),
      AssessmentEpisodeEntity(
        episodeId = 3,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        author = author,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(2),
      )
    )
    every { assessmentSchemaService.getQuestionsForSchemaCode(newEpisode.assessmentSchemaCode) } returns schemaQuestions
    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentSchemaCode(AssessmentSchemaCode.ROSH) } returns listOf(
      CloneAssessmentExcludedQuestionsEntity(
        12345,
        AssessmentSchemaCode.ROSH,
        "question_2"
      ),
      CloneAssessmentExcludedQuestionsEntity(
        23456,
        AssessmentSchemaCode.ROSH,
        "table_1"
      )
    )

    val result = episodeService.prepopulateFromPreviousEpisodes(newEpisode, mixedPreviousEpisodes)

    val expectedAnswers = mutableMapOf(
      "question_1" to listOf("answer_1")
    )

    assertThat(result.answers).containsExactlyEntriesOf(expectedAnswers)
  }

  @Test
  fun `copies answers and tables from previous episode`() {
    val schemaQuestions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
      TableQuestionDto(tableCode = "table_1")
    )

    val mixedPreviousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        author = author,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "question_1" to listOf("answer_1"),
          "question_2" to listOf("answer_2")
        )
      ),
      AssessmentEpisodeEntity(
        episodeId = 3,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        author = author,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(2),
      )
    )
    every { assessmentSchemaService.getQuestionsForSchemaCode(newEpisode.assessmentSchemaCode) } returns schemaQuestions

    val result = episodeService.prepopulateFromPreviousEpisodes(newEpisode, mixedPreviousEpisodes)

    val expectedAnswers = mutableMapOf(
      "question_1" to listOf("answer_1"),
      "question_2" to listOf("answer_2")
    )

    assertThat(result.answers).containsExactlyInAnyOrderEntriesOf(expectedAnswers)
  }

  @Test
  fun `new episode unchanged as no previous episodes`() {
    val schemaQuestions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
    )
    every { assessmentSchemaService.getQuestionsForSchemaCode(newEpisode.assessmentSchemaCode) } returns schemaQuestions

    val emptyPreviousEpisode = emptyList<AssessmentEpisodeEntity>()

    val result = episodeService.prepopulateFromPreviousEpisodes(newEpisode, emptyPreviousEpisode).answers

    assertThat(result).isEmpty()
  }

  @Test
  fun `new episode unchanged as empty answers in previous episodes`() {
    val previousEpisodesNoAnswers = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        author = author,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf()
      )
    )

    val schemaQuestions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
    )
    every { assessmentSchemaService.getQuestionsForSchemaCode(newEpisode.assessmentSchemaCode) } returns schemaQuestions

    val result = episodeService.prepopulateFromPreviousEpisodes(newEpisode, previousEpisodesNoAnswers).answers

    assertThat(result).isEmpty()
  }

  @Test
  fun `existing episode older than 55 weeks will be ignored`() {

    val previousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        author = author,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusWeeks(1),
        answers = mutableMapOf(
          "question_1" to listOf("answer_1"),
          "question_2" to listOf("answer_2")
        )
      ),
      AssessmentEpisodeEntity(
        episodeId = 3,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        author = author,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusWeeks(55).minusDays(1),
        answers = mutableMapOf(
          "question_3" to listOf("answer_3"),
          "question_4" to listOf("answer_4")
        )
      )
    )

    val schemaQuestions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
      GroupQuestionDto(questionCode = "question_3"),
      GroupQuestionDto(questionCode = "question_4")
    )

    every { assessmentSchemaService.getQuestionsForSchemaCode(newEpisode.assessmentSchemaCode) } returns schemaQuestions

    val result = episodeService.prepopulateFromPreviousEpisodes(newEpisode, previousEpisodes).answers

    val expectedAnswers = mutableMapOf(
      "question_1" to listOf("answer_1"),
      "question_2" to listOf("answer_2")
    )

    assertThat(result).containsExactlyInAnyOrderEntriesOf(expectedAnswers)
    assertThat(result).doesNotContainKey("question_3")
    assertThat(result).doesNotContainKey("question_4")
  }

  @Test
  fun `get structured answers from Delius json for GP`() {
    val personalContactJson = """[
  {
    "personalContactId": 2500125492,
    "relationship": "Friend",
    "startDate": "2020-12-15T00:00:00",
    "title": "Mr",
    "firstName": "UPW",
    "surname": "TESTING",
    "mobileNumber": "07123456789",
    "emailAddress": "test@test.com",
    "notes": "ARN Mapping Value testing - 28/10/2022 - ARN-631",
    "gender": "Male",
    "relationshipType": {
      "code": "RT02",
      "description": "Emergency Contact"
    },
    "createdDatetime": "2021-10-28T18:57:56",
    "lastUpdatedDatetime": "2021-10-28T18:57:56",
    "address": {
      "addressNumber": "102",
      "buildingName": "Petty France",
      "streetName": "Central London",
      "district": "London",
      "town": "London",
      "county": "London",
      "postcode": "SW1H 9AJ",
      "telephoneNumber": "020 2000 0000"
    },
    "isActive": true
  },
  {
    "personalContactId": 2500125493,
    "relationship": "Family Doctor",
    "startDate": "2021-04-14T00:00:00",
    "title": "Dr",
    "firstName": "Charles",
    "surname": "Europe",
    "mobileNumber": "07123456789",
    "emailAddress": "gp@gp.com",
    "notes": "ARN Mapping Value testing - 28/10/2022 - ARN-631",
    "gender": "Male",
    "relationshipType": {
      "code": "RT02",
      "description": "GP"
    },
    "createdDatetime": "2021-10-28T19:02:03",
    "lastUpdatedDatetime": "2021-10-28T19:02:03",
    "address": {
      "addressNumber": "32",
      "buildingName": "MOJ Building",
      "streetName": "Scotland Street",
      "district": "Sheffield",
      "town": "Sheffield",
      "county": "South Yorkshire",
      "postcode": "S3 7DQ",
      "telephoneNumber": "020 2123 5678"
    },
    "isActive": true
  }
]"""

    val docContext: DocumentContext = JsonPath.parse(personalContactJson)
    val externalSourceGPObjectMapping = ExternalSourceQuestionSchemaDto(
      questionCode = "gp_details",
      externalSource = "Delius",
      jsonPathField = "\$[?(@.relationshipType.code=='RT02')]",
      fieldType = "structure",
      ifEmpty = false,
    )
    val externalSourceGPQuestions = listOf(
      ExternalSourceQuestionSchemaDto(
        questionCode = "gp_first_name",
        externalSource = "Delius",
        jsonPathField = "firstName",
        fieldType = "structuredAnswer",
        ifEmpty = false,
        structuredQuestionCode = "gp_details"
      ),
      ExternalSourceQuestionSchemaDto(
        questionCode = "gp_family_name",
        externalSource = "Delius",
        jsonPathField = "surname",
        fieldType = "structuredAnswer",
        ifEmpty = false,
        structuredQuestionCode = "gp_details"
      ),
      ExternalSourceQuestionSchemaDto(
        questionCode = "gp_address_postcode",
        externalSource = "Delius",
        jsonPathField = "address.postcode",
        fieldType = "structuredAnswer",
        ifEmpty = false,
        structuredQuestionCode = "gp_details"
      )
    )

    val result =
      episodeService.getStructuredAnswersFromSourceData(docContext, externalSourceGPObjectMapping, externalSourceGPQuestions)

    assertThat(result).contains(
      """{"gp_first_name":["UPW"],"gp_family_name":["TESTING"],"gp_address_postcode":["SW1H 9AJ"]}""".trimIndent(),
      """{"gp_first_name":["Charles"],"gp_family_name":["Europe"],"gp_address_postcode":["S3 7DQ"]}"""
    )
  }
}
