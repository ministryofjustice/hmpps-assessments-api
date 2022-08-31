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
import uk.gov.justice.digital.assessments.api.EmergencyContactDetailsAnswerDto
import uk.gov.justice.digital.assessments.api.GPDetailsAnswerDto
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.TableQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType.ROSH
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType.RSR
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType.UPW
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.TableRow
import uk.gov.justice.digital.assessments.jpa.entities.assessments.TableRows
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Tables
import uk.gov.justice.digital.assessments.jpa.entities.refdata.CloneAssessmentExcludedQuestionsEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.CloneAssessmentExcludedQuestionsRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentApiRestClient
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.services.dto.ExternalSource
import uk.gov.justice.digital.assessments.services.dto.ExternalSourceQuestionDto
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
@DisplayName("Episode Service Tests")
class EpisodeServiceTest {

  private val questionService: QuestionService = mockk()
  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val assessmentApiRestClient: AssessmentApiRestClient = mockk()
  private val assessmentReferenceDataService: AssessmentReferenceDataService = mockk()
  private val cloneAssessmentExcludedQuestionsRepository: CloneAssessmentExcludedQuestionsRepository = mockk()

  private val episodeService = EpisodeService(
    questionService,
    communityApiRestClient,
    assessmentApiRestClient,
    assessmentReferenceDataService,
    cloneAssessmentExcludedQuestionsRepository
  )

  private lateinit var newEpisode: AssessmentEpisodeEntity

  private val authorEntity = AuthorEntity(
    userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"
  )

  companion object {
    private const val CRN: String = "someCrn"
    private const val ENDPOINT_URL = "some/endpoint"
  }

  @BeforeEach
  fun setup() {
    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentType(ROSH) } returns emptyList()
    newEpisode = createEpisode(ROSH)
  }

  private fun createEpisode(assessmentType: AssessmentType): AssessmentEpisodeEntity {
    return AssessmentEpisodeEntity(
      episodeId = 1,
      assessmentType = assessmentType,
      author = authorEntity,
      assessment = AssessmentEntity(
        subject = SubjectEntity(
          crn = CRN,
          dateOfBirth = LocalDate.parse("1999-12-31")
        )
      )
    )
  }

  @Test
  fun `should pre-populate gender identity answers from Delius as an external source for a UPW assessment type`() {
    // Given
    newEpisode = createEpisode(UPW)

    val questions: List<ExternalSourceQuestionDto> = createGenderIdentityExternalSourceQuestionList(ENDPOINT_URL)
    every { questionService.getAllQuestions().withExternalSource(UPW) } returns questions

    val json = this::class.java.getResource("/json/deliusOffender.json")?.readText()
    every {
      communityApiRestClient.getOffenderJson(crn = CRN, externalSourceEndpoint = ENDPOINT_URL)
    } returns json

    // When
    val episodeEntity = episodeService.prePopulateFromExternalSources(newEpisode, UPW)

    // Then
    val expectedAnswers = mutableMapOf(
      "gender_identity" to listOf("PREFER_TO_SELF_DESCRIBE"),
    )
    assertThat(episodeEntity.answers).containsExactlyEntriesOf(expectedAnswers)
  }

  @Test
  fun `should not add contact address from previous episode if present in Delius`() {
    // Given
    newEpisode = createEpisode(UPW)

    val questions: List<ExternalSourceQuestionDto> = createContactDetailsAddressExternalSourceQuestionList(ENDPOINT_URL)
    every { questionService.getAllQuestions().withExternalSource(UPW) } returns questions
    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentType(UPW) } returns emptyList()

    val json = this::class.java.getResource("/json/deliusOffender.json")?.readText()
    every {
      communityApiRestClient.getOffenderJson(crn = CRN, externalSourceEndpoint = ENDPOINT_URL)
    } returns json

    val questionDtos = listOf(
      GroupQuestionDto(questionCode = "contact_address_house_number"),
      GroupQuestionDto(questionCode = "contact_address_building_name"),
      GroupQuestionDto(questionCode = "contact_address_street_name"),
      GroupQuestionDto(questionCode = "contact_address_postcode"),
      GroupQuestionDto(questionCode = "contact_address_town_or_city"),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questionDtos

    val previousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "contact_address_house_number" to listOf("12"),
          "contact_address_building_name" to listOf("The Gables"),
          "contact_address_street_name" to listOf("High Street"),
          "contact_address_postcode" to listOf("S11 8JK"),
          "contact_address_town_or_city" to listOf("Leeds")
        )
      ),
    )

    // When
    val episodeEntity = episodeService.prePopulateFromExternalSources(newEpisode, UPW)
    episodeService.prePopulateFromPreviousEpisodes(newEpisode, previousEpisodes)

    // Then
    val expectedAnswers = mutableMapOf(
      "contact_address_house_number" to listOf("32"),
      "contact_address_building_name" to listOf("HMPPS Digital Studio"),
      "contact_address_street_name" to emptyList(),
      "contact_address_postcode" to listOf("S3 7BS"),
      "contact_address_town_or_city" to listOf("Sheffield")
    )
    assertThat(episodeEntity.answers).containsAllEntriesOf(expectedAnswers)
  }

  @Test
  fun `should not add personal contacts from previous episode if present in Delius`() {
    // Given
    newEpisode = createEpisode(UPW)

    val questions: List<ExternalSourceQuestionDto> = createPersonalContactExternalSourceQuestionList(ENDPOINT_URL)
    every { questionService.getAllQuestions().withExternalSource(UPW) } returns questions
    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentType(UPW) } returns emptyList()

    val personalContactsJson = this::class.java.getResource("/json/deliusPersonalContacts.json")?.readText()
    every {
      communityApiRestClient.getOffenderJson(crn = CRN, externalSourceEndpoint = ENDPOINT_URL)
    } returns personalContactsJson

    val questionDtos = listOf(
      GroupQuestionDto(questionCode = "gp_details"),
      GroupQuestionDto(questionCode = "emergency_contact_details"),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questionDtos

    val previousEpisodes = createPreviousEpisodePersonalContacts()

    // When
    val episodeEntity = episodeService.prePopulateFromExternalSources(newEpisode, UPW)
    episodeService.prePopulateFromPreviousEpisodes(newEpisode, previousEpisodes)

    // Then
    val gpDetailsAnswerDtos = episodeEntity.answers["gp_details"] as List<GPDetailsAnswerDto>
    assertThat(gpDetailsAnswerDtos.size).isEqualTo(1)
    assertThat(gpDetailsAnswerDtos[0].name).isEqualTo(listOf("Charles Europe"))
    assertThat(gpDetailsAnswerDtos[0].buildingName).isEqualTo(listOf(null))

    val emergencyContactDetailsAnswerDtos =
      episodeEntity.answers["emergency_contact_details"] as List<EmergencyContactDetailsAnswerDto>
    assertThat(emergencyContactDetailsAnswerDtos.size).isEqualTo(1)
    assertThat(emergencyContactDetailsAnswerDtos[0].relationship).isEqualTo(listOf("Friend"))
    assertThat(emergencyContactDetailsAnswerDtos[0].buildingName).isEqualTo(listOf("Petty France"))
  }

  private fun createPreviousEpisodePersonalContacts() = listOf(
    AssessmentEpisodeEntity(
      episodeId = 2,
      assessmentType = UPW,
      author = authorEntity,
      assessment = AssessmentEntity(),
      endDate = LocalDateTime.now().minusDays(1),
      answers = mutableMapOf(
        "gp_details" to listOf(
          GPDetailsAnswerDto(
            name = listOf("Some previous episode name"),
            buildingName = listOf("Some previous episode building name")
          ),
          GPDetailsAnswerDto(
            name = listOf("Some other previous episode name"),
            buildingName = listOf("Some other previous episode building name")
          )
        ),
        "emergency_contact_details" to listOf(
          EmergencyContactDetailsAnswerDto(
            relationship = listOf("Some previous episode relationship"),
            buildingName = listOf("Some previous episode building name")
          ),
          EmergencyContactDetailsAnswerDto(
            relationship = listOf("Some other previous episode relationship"),
            buildingName = listOf("Some other previous episode building name")
          )
        ),
      )
    ),
  )

  private fun createPersonalContactExternalSourceQuestionList(endpoint: String) = listOf(
    ExternalSourceQuestionDto(
      "gp_details",
      ExternalSource.DELIUS.name,
      "\$[?(@.relationshipType.code=='RT02'&&@.isActive==true)]",
      "structured",
      externalSourceEndpoint = endpoint,
      ifEmpty = false,
    ),
    ExternalSourceQuestionDto(
      "emergency_contact_details",
      ExternalSource.DELIUS.name,
      "\$[?(@.relationshipType.code=='ME'&&@.isActive==true)]",
      "structured",
      externalSourceEndpoint = endpoint,
      ifEmpty = false,
    )
  )

  @Test
  fun `should not add answers from previous episode if present in Delius`() {
    // Given
    newEpisode = createEpisode(UPW)

    val questions: List<ExternalSourceQuestionDto> = createGenderIdentityExternalSourceQuestionList(ENDPOINT_URL)
    every { questionService.getAllQuestions().withExternalSource(UPW) } returns questions
    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentType(UPW) } returns emptyList()

    val json = this::class.java.getResource("/json/deliusOffender.json")?.readText()
    every {
      communityApiRestClient.getOffenderJson(crn = CRN, externalSourceEndpoint = ENDPOINT_URL)
    } returns json

    val questionDtos = listOf(
      GroupQuestionDto(questionCode = "gender_identity"),
      GroupQuestionDto(questionCode = "question_2"),
      TableQuestionDto(tableCode = "table_1")
    )

    val previousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = UPW,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "gender_identity" to listOf("NON_BINARY"),
          "question_2" to listOf("answer_2")
        )
      ),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questionDtos

    // When
    val episodeEntity = episodeService.prePopulateFromExternalSources(newEpisode, UPW)
    episodeService.prePopulateFromPreviousEpisodes(newEpisode, previousEpisodes)

    // Then
    val expectedAnswers = mutableMapOf(
      "gender_identity" to listOf("PREFER_TO_SELF_DESCRIBE"),
      "question_2" to listOf("answer_2"),
    )
    assertThat(episodeEntity.answers).containsExactlyEntriesOf(expectedAnswers)
  }

  private fun createContactDetailsAddressExternalSourceQuestionList(endpoint: String): List<ExternalSourceQuestionDto> {

    return listOf(
      ExternalSourceQuestionDto(
        "contact_address_building_name",
        ExternalSource.DELIUS.name,
        "\$.contactDetails.addresses[?(@.status.code=='M')].buildingName",
        null,
        externalSourceEndpoint = endpoint,
        ifEmpty = false,
        mappedValue = null
      ),
      ExternalSourceQuestionDto(
        "contact_address_house_number",
        ExternalSource.DELIUS.name,
        "\$.contactDetails.addresses[?(@.status.code=='M')].addressNumber",
        null,
        externalSourceEndpoint = endpoint,
        ifEmpty = false,
        mappedValue = null
      ),
      ExternalSourceQuestionDto(
        "contact_address_street_name",
        ExternalSource.DELIUS.name,
        "\$.contactDetails.addresses[?(@.status.code=='M')].streetName",
        null,
        externalSourceEndpoint = endpoint,
        ifEmpty = false,
        mappedValue = null
      ),
      ExternalSourceQuestionDto(
        "contact_address_town_or_city",
        ExternalSource.DELIUS.name,
        "\$.contactDetails.addresses[?(@.status.code=='M')].town",
        null,
        externalSourceEndpoint = endpoint,
        ifEmpty = false,
        mappedValue = null
      ),
      ExternalSourceQuestionDto(
        "contact_address_postcode",
        ExternalSource.DELIUS.name,
        "\$.contactDetails.addresses[?(@.status.code=='M')].postcode",
        null,
        externalSourceEndpoint = endpoint,
        ifEmpty = false,
        mappedValue = null
      ),
    )
  }

  private fun createGenderIdentityExternalSourceQuestionList(endpoint: String): List<ExternalSourceQuestionDto> {
    return listOf(
      ExternalSourceQuestionDto(
        "gender_identity",
        ExternalSource.DELIUS.name,
        "\$[?(@.offenderProfile.genderIdentity == 'Prefer to self-describe')].offenderProfile.genderIdentity",
        "mapped",
        externalSourceEndpoint = endpoint,
        ifEmpty = false,
        mappedValue = "PREFER_TO_SELF_DESCRIBE"
      ),
      ExternalSourceQuestionDto(
        "gender_identity",
        ExternalSource.DELIUS.name,
        "\$[?(@.offenderProfile.genderIdentity == 'Male')].offenderProfile.genderIdentity",
        "mapped",
        externalSourceEndpoint = endpoint,
        ifEmpty = false,
        mappedValue = "MALE"
      ),
      ExternalSourceQuestionDto(
        "gender_identity",
        ExternalSource.DELIUS.name,
        "\$[?(@.offenderProfile.genderIdentity == 'Female')].offenderProfile.genderIdentity",
        "mapped",
        externalSourceEndpoint = endpoint,
        ifEmpty = false,
        mappedValue = "FEMALE"
      ),
    )
  }

  @Test
  fun `should pre-populate answers from external source OASys`() {
    newEpisode = createEpisode(RSR)

    val questions: List<ExternalSourceQuestionDto> = listOf(
      ExternalSourceQuestionDto(
        "question_1", ExternalSource.OASYS.name,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='1.24')].answer[0]",
        "array", ENDPOINT_URL, ifEmpty = false
      ),
      ExternalSourceQuestionDto(
        "question_2", ExternalSource.OASYS.name,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='1.26')].answer[0]",
        "array", ENDPOINT_URL, ifEmpty = false
      ),
    )

    every { questionService.getAllQuestions().withExternalSource(RSR) } returns questions

    val json = this::class.java.getResource("/json/oasysLatestRSRShortAssessment.json")?.readText()

    every {
      assessmentApiRestClient.getOASysLatestAssessment(
        crn = CRN,
        status = listOf("SIGNED", "COMPLETE"),
        types = listOf("LAYER_1", "LAYER_3"),
        cutoffDate = any()
      )
    } returns json

    val result =
      episodeService.prePopulateFromExternalSources(newEpisode, RSR)

    val expectedAnswers = mutableMapOf(
      "question_1" to listOf("answer 1"),
      "question_2" to listOf("answer 2")
    )
    assertThat(result.answers).containsExactlyEntriesOf(expectedAnswers)
  }

  @Test
  fun `does not pre-populate answers from external source as OASys does not return an assessment`() {
    // Given
    newEpisode = createEpisode(RSR)

    val questions: List<ExternalSourceQuestionDto> = listOf(
      ExternalSourceQuestionDto(
        "question_1", ExternalSource.OASYS.name,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='1.24')].answer[0]",
        "array", ENDPOINT_URL, ifEmpty = false
      ),
      ExternalSourceQuestionDto(
        "question_2", ExternalSource.OASYS.name,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='1.26')].answer[0]",
        "array", ENDPOINT_URL, ifEmpty = false
      ),
    )

    val now = LocalDateTime.now()
    val latestEndDate = now.minusWeeks(5)

    val previousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = RSR,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = now,
        answers = mutableMapOf(
          "question_1" to listOf("previous answer 1"),
          "question_2" to listOf("previous answer 2")
        )
      ),
      AssessmentEpisodeEntity(
        episodeId = 3,
        assessmentType = RSR,
        author = authorEntity,
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
        crn = CRN,
        status = listOf("SIGNED", "COMPLETE"),
        types = listOf("LAYER_1", "LAYER_3"),
        cutoffDate = latestEndDate
      )
    } returns null

    // When
    val result = episodeService.prePopulateFromExternalSources(newEpisode, RSR)

    // Then
    assertThat(result.answers).isEmpty()
  }

  @Test
  fun `does not pre-populate answers from external source as no matching answers in OASys`() {
    val externalSource = "OASYS"
    newEpisode = createEpisode(RSR)

    val questions: List<ExternalSourceQuestionDto> = listOf(
      ExternalSourceQuestionDto(
        "question_1", externalSource,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='XXX')].answer[0]",
        "array", ENDPOINT_URL, ifEmpty = false
      ),
      ExternalSourceQuestionDto(
        "question_2", externalSource,
        "\$.sections[?(@.section=='1')].answers[?(@.question=='YYY')].answer[0]",
        "array", ENDPOINT_URL, ifEmpty = false
      ),
    )

    every { questionService.getAllQuestions().withExternalSource(RSR) } returns questions

    val json = this::class.java.getResource("/json/oasysLatestRSRShortAssessment.json")?.readText()

    every {
      assessmentApiRestClient.getOASysLatestAssessment(
        crn = CRN,
        status = listOf("SIGNED", "COMPLETE"),
        types = listOf("LAYER_1", "LAYER_3"),
        cutoffDate = any()
      )
    } returns json

    val result = episodeService.prePopulateFromExternalSources(newEpisode, RSR)

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
        assessmentType = ROSH,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "question_1" to listOf("answer_1"),
          "question_2" to listOf("answer_2")
        )
      ),
      AssessmentEpisodeEntity(
        episodeId = 3,
        assessmentType = ROSH,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(2),
        tables = table1
      )
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns schemaQuestions
    every { cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentType(ROSH) } returns listOf(
      CloneAssessmentExcludedQuestionsEntity(
        12345,
        ROSH,
        "question_2"
      ),
      CloneAssessmentExcludedQuestionsEntity(
        23456,
        ROSH,
        "table_1"
      )
    )

    val result = episodeService.prePopulateFromPreviousEpisodes(newEpisode, mixedPreviousEpisodes)

    val expectedAnswers = mutableMapOf(
      "question_1" to listOf("answer_1")
    )

    assertThat(result.answers).containsExactlyEntriesOf(expectedAnswers)
    assertThat(result.tables).isEmpty()
  }

  @Test
  fun `copies answers and tables from previous episode`() {
    // Given
    val questions = listOf(
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

    val table1: Tables = mutableMapOf(
      "table_1" to tableRows1
    )

    val mixedPreviousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = ROSH,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf(
          "question_1" to listOf("answer_1"),
          "question_2" to listOf("answer_2")
        )
      ),
      AssessmentEpisodeEntity(
        episodeId = 3,
        assessmentType = ROSH,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(2),
        tables = table1
      )
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questions

    // When
    val result = episodeService.prePopulateFromPreviousEpisodes(newEpisode, mixedPreviousEpisodes)

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

    // Then
    assertThat(result.answers).containsExactlyInAnyOrderEntriesOf(expectedAnswers)
    assertThat(result.tables).containsExactlyInAnyOrderEntriesOf(expectedTable1)
  }

  @Test
  fun `new episode unchanged as no previous episodes`() {
    val questions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questions

    val emptyPreviousEpisode = emptyList<AssessmentEpisodeEntity>()

    val result = episodeService.prePopulateFromPreviousEpisodes(newEpisode, emptyPreviousEpisode).answers

    assertThat(result).isEmpty()
  }

  @Test
  fun `new episode unchanged as empty answers in previous episodes`() {
    val previousEpisodesNoAnswers = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = ROSH,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusDays(1),
        answers = mutableMapOf()
      )
    )

    val questions = listOf(
      GroupQuestionDto(questionCode = "question_1"),
      GroupQuestionDto(questionCode = "question_2"),
    )
    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns questions

    val result = episodeService.prePopulateFromPreviousEpisodes(newEpisode, previousEpisodesNoAnswers).answers

    assertThat(result).isEmpty()
  }

  @Test
  fun `existing episode older than 55 weeks will be ignored`() {

    val previousEpisodes = listOf(
      AssessmentEpisodeEntity(
        episodeId = 2,
        assessmentType = ROSH,
        author = authorEntity,
        assessment = AssessmentEntity(),
        endDate = LocalDateTime.now().minusWeeks(1),
        answers = mutableMapOf(
          "question_1" to listOf("answer_1"),
          "question_2" to listOf("answer_2")
        )
      ),
      AssessmentEpisodeEntity(
        episodeId = 3,
        assessmentType = ROSH,
        author = authorEntity,
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

    every { assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType) } returns schemaQuestions

    val result = episodeService.prePopulateFromPreviousEpisodes(newEpisode, previousEpisodes).answers

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
    val personalContactJson = this::class.java.getResource("/json/deliusPersonalContacts.json")?.readText()

    val docContext: DocumentContext = JsonPath.parse(personalContactJson)
    val externalSourceGPObjectMapping = ExternalSourceQuestionDto(
      questionCode = "gp_details",
      externalSource = "Delius",
      jsonPathField = "\$[?(@.relationshipType.code=='RT02')]",
      fieldType = "structured",
      ifEmpty = false,
    )

    val gpDetails = episodeService.getStructuredAnswersFromSourceData(docContext, externalSourceGPObjectMapping)
    val gp1 = gpDetails?.get(0) as GPDetailsAnswerDto

    assertThat(gp1.name).isEqualTo(listOf("Charles Europe"))
    assertThat(gp1.postcode).isEqualTo(listOf("S3 7DQ"))
    assertThat(gp1.practiceName).isEqualTo(emptyList<String>())

    assertThat(gpDetails).hasSize(1)
  }
}
