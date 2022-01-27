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
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.TableRow
import uk.gov.justice.digital.assessments.jpa.entities.assessments.TableRows
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Tables
import uk.gov.justice.digital.assessments.jpa.entities.refdata.CloneAssessmentExcludedQuestionsEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.CloneAssessmentExcludedQuestionsRepository
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.dto.ExternalSourceQuestionSchemaDto
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@DisplayName("Episode Service Tests")
class EpisodeServiceTest {

  private val questionService: QuestionService = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()
  private val cloneAssessmentExcludedQuestionsRepository: CloneAssessmentExcludedQuestionsRepository = mockk()

  private val episodeService = EpisodeService(
    questionService,
    courtCaseRestClient,
    communityApiRestClient,
    assessmentSchemaService,
    cloneAssessmentExcludedQuestionsRepository
  )

  private lateinit var newEpisode: AssessmentEpisodeEntity
  private lateinit var previousEpisodes: List<AssessmentEpisodeEntity>

  private val author = AuthorEntity(
    userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"
  )

  @BeforeEach
  fun setup() {
    newEpisode = AssessmentEpisodeEntity(
      episodeId = 1,
      assessmentSchemaCode = AssessmentSchemaCode.ROSH,
      author = author,
      assessment = AssessmentEntity()
    )
    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentSchemaCode(AssessmentSchemaCode.ROSH) } returns emptyList()
  }

  @Test
  fun `copies answers and tables from previous episode ignoring excluded questions`() {
    val schemaQuestions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
      TableQuestionDto(tableCode = "table_1")
    )

    val tablerow1: TableRow = mutableMapOf(
      "tablerow_1" to listOf("tablerow_answer_1"),
      "tablerow_2" to listOf("tablerow_answer_2")
    )

    val tableRows1: TableRows = mutableListOf(
      tablerow1
    )

    var table1: Tables = mutableMapOf(
      "table_1" to tableRows1
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
        tables = table1
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
    assertThat(result.tables).isEmpty()
  }

  @Test
  fun `copies answers and tables from previous episode`() {
    val schemaQuestions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
      TableQuestionDto(tableCode = "table_1")
    )

    val tablerow1: TableRow = mutableMapOf(
      "tablerow_1" to listOf("tablerow_answer_1"),
      "tablerow_2" to listOf("tablerow_answer_2")
    )

    val tableRows1: TableRows = mutableListOf(
      tablerow1
    )

    var table1: Tables = mutableMapOf(
      "table_1" to tableRows1
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
        tables = table1
      )
    )
    every { assessmentSchemaService.getQuestionsForSchemaCode(newEpisode.assessmentSchemaCode) } returns schemaQuestions

    val result = episodeService.prepopulateFromPreviousEpisodes(newEpisode, mixedPreviousEpisodes)

    val expectedAnswers = mutableMapOf(
      "question_1" to listOf("answer_1"),
      "question_2" to listOf("answer_2")
    )

    val expectedTableRow: TableRow = mutableMapOf(
      "tablerow_1" to listOf("tablerow_answer_1"),
      "tablerow_2" to listOf("tablerow_answer_2")
    )

    val expectedTableRows: TableRows = mutableListOf(
      expectedTableRow
    )

    var expectedTable1: Tables = mutableMapOf(
      "table_1" to expectedTableRows
    )

    assertThat(result.answers).containsExactlyInAnyOrderEntriesOf(expectedAnswers)
    assertThat(result.tables).containsExactlyInAnyOrderEntriesOf(expectedTable1)
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
  fun `test`() {

    val json = """[
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
    val docContext: DocumentContext = JsonPath.parse(json)
    val question = ExternalSourceQuestionSchemaDto(
      questionCode = "gp_details",
      externalSource = "Delius",
      jsonPathField = "\$[?(@.relationshipType.code=='RT02')]",
      fieldType = "table",
      externalSourceEndpoint = "someUrl",
      mappedValue = null,
      ifEmpty = false,
    )

    val childQuestionFirstName = ExternalSourceQuestionSchemaDto(
      questionCode = "gp_first_name",
      externalSource = "Delius",
      jsonPathField = "$.firstName",
      fieldType = "table_question",
      externalSourceEndpoint = "someUrl",
      mappedValue = null,
      ifEmpty = false,
      parentQuestionCode = "gp_details"
    )

    val childQuestionFamilyName = ExternalSourceQuestionSchemaDto(
      questionCode = "gp_family_name",
      externalSource = "Delius",
      jsonPathField = "$.surname",
      fieldType = "table_question",
      externalSourceEndpoint = "someUrl",
      mappedValue = null,
      ifEmpty = false,
      parentQuestionCode = "gp_details"
    )

    episodeService.buildTable(docContext, question, listOf(childQuestionFirstName, childQuestionFamilyName))
  }

  @Test
  fun `build table rows returns TableRows`() {

    val json = """[
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
    val docContext: DocumentContext = JsonPath.parse(json)
    val question = ExternalSourceQuestionSchemaDto(
      questionCode = "gp_details",
      externalSource = "Delius",
      jsonPathField = "\$[?(@.relationshipType.code=='RT02')]",
      fieldType = "table",
      externalSourceEndpoint = "someUrl",
      mappedValue = null,
      ifEmpty = false,
    )

    val childQuestionFirstName = ExternalSourceQuestionSchemaDto(
      questionCode = "gp_first_name",
      externalSource = "Delius",
      jsonPathField = "firstName",
      fieldType = "table_question",
      externalSourceEndpoint = "someUrl",
      mappedValue = null,
      ifEmpty = false,
      parentQuestionCode = "gp_details"
    )

    val childQuestionFamilyName = ExternalSourceQuestionSchemaDto(
      questionCode = "gp_family_name",
      externalSource = "Delius",
      jsonPathField = "surname",
      fieldType = "table_question",
      externalSourceEndpoint = "someUrl",
      mappedValue = null,
      ifEmpty = false,
      parentQuestionCode = "gp_details"
    )

    val childQuestionGPAddressPostcode = ExternalSourceQuestionSchemaDto(
      questionCode = "gp_address_postcode",
      externalSource = "Delius",
      jsonPathField = "address.postcode",
      fieldType = "table_question",
      externalSourceEndpoint = "someUrl",
      mappedValue = null,
      ifEmpty = false,
      parentQuestionCode = "gp_details"
    )

    val result =
      episodeService.buildTable(docContext, question, listOf(childQuestionFirstName, childQuestionFamilyName, childQuestionGPAddressPostcode))

    val expectedTableRow1: TableRow = mutableMapOf(
      "gp_first_name" to listOf("UPW"),
      "gp_family_name" to listOf("TESTING"),
      "gp_address_postcode" to listOf("SW1H 9AJ")
    )

    val expectedTableRow2: TableRow = mutableMapOf(
      "gp_first_name" to listOf("Charles"),
      "gp_family_name" to listOf("Europe"),
      "gp_address_postcode" to listOf("S3 7DQ")
    )

    val expectedTableRows: TableRows = mutableListOf(
      expectedTableRow1, expectedTableRow2
    )

    assertThat(result).isEqualTo(expectedTableRows)
  }
}
