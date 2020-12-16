package uk.gov.justice.digital.assessments.services


import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.api.AnswerDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.*
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tests")
class AssessmentServiceTest {
    private val assessmentRepository: AssessmentRepository = mockk()
    private val subjectRepository: SubjectRepository = mockk()
    private val questionService: QuestionService = mockk()
    private val courtCaseRestClient: CourtCaseRestClient = mockk()

    private val assessmentsService = AssessmentService(
            assessmentRepository,
            subjectRepository,
            questionService,
            courtCaseRestClient)

    private val assessmentUuid = UUID.randomUUID()
    private val assessmentId = 1L

    private val episodeId1 = 1L
    private val episodeId2 = 2L
    private val episodeId3 = 3L

    private val episodeUuid = UUID.randomUUID()

    private val existingAnswerUuid = UUID.randomUUID()
    private val existingQuestionUuid = UUID.randomUUID()

    private val question1Uuid = UUID.randomUUID()
    private val question2Uuid = UUID.randomUUID()
    private val answer1Uuid = UUID.randomUUID()
    private val answer2Uuid = UUID.randomUUID()
    private val answer3Uuid = UUID.randomUUID()

    @Test
    fun `should save new assessment`() {
        every { assessmentRepository.findBySupervisionId(any()) } returns null
        every { assessmentRepository.save(any()) } returns AssessmentEntity(assessmentId = assessmentId)

        assessmentsService.createNewAssessment(CreateAssessmentDto("SupervisionId"))
        verify(exactly = 1) { assessmentRepository.save(any()) }
    }

    @Test
    fun `should return existing assessment if one exists`() {
        every { assessmentRepository.findBySupervisionId(any()) } returns AssessmentEntity(assessmentId = assessmentId, assessmentUuid = assessmentUuid)

        val assessmentDto = assessmentsService.createNewAssessment(CreateAssessmentDto("SupervisionId"))
        assertThat(assessmentDto.assessmentUuid).isEqualTo(assessmentUuid)
        verify(exactly = 0) { assessmentRepository.save(any()) }
    }

    @Test
    fun `should create new episode`() {
        val assessment: AssessmentEntity = mockk()
        every { assessment.assessmentUuid } returns assessmentUuid
        every { assessment.assessmentId } returns 0
        every { assessment.newEpisode("Change of Circs") } returns AssessmentEpisodeEntity(episodeId = episodeId1, assessment = assessment)
        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val episodeDto = assessmentsService.createNewEpisode(assessmentUuid, "Change of Circs")

        assertThat(episodeDto?.assessmentUuid).isEqualTo(assessmentUuid)
        assertThat(episodeDto?.episodeId).isEqualTo(episodeId1)
    }

    @Test
    fun `should return all episodes for an assessment`() {
        val assessment = AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = episodeId1, changeReason = "Change of Circs 1"),
                        AssessmentEpisodeEntity(episodeId = episodeId2, changeReason = "Change of Circs 2"))
        )

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val episodeDtos = assessmentsService.getAssessmentEpisodes(assessmentUuid)
        assertThat(episodeDtos).hasSize(2)
    }

    @Test
    fun `get episodes throws exception if assessment does not exist`() {

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

        assertThatThrownBy { assessmentsService.getAssessmentEpisodes(assessmentUuid) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("Assessment $assessmentUuid not found")
    }

    @Test
    fun `should return latest episode for an assessment`() {
        val assessment = AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = episodeId1, changeReason = "Change of Circs 1", endDate = LocalDateTime.now().minusDays(1)),
                        AssessmentEpisodeEntity(episodeId = episodeId2, changeReason = "Change of Circs 2"))
        )

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val episodeDto = assessmentsService.getCurrentAssessmentEpisode(assessmentUuid)
        assertThat(episodeDto.episodeId).isEqualTo(episodeId2)
    }

    @Test
    fun `get current episode throws exception if assessment does not exist`() {

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

        assertThatThrownBy { assessmentsService.getCurrentAssessmentEpisode(assessmentUuid) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("Assessment $assessmentUuid not found")
    }

    @Test
    fun `get current episode throws exception if no current episode exists`() {

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns null

        assertThatThrownBy { assessmentsService.getCurrentAssessmentEpisode(assessmentUuid) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("Assessment $assessmentUuid not found")
    }

    @Test
    fun `update episode should throw exception if episode does not exist`() {

        val assessment = AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeUuid = UUID.randomUUID(), episodeId = episodeId2, changeReason = "Change of Circs 2"))
        )

        val updatedAnswers = UpdateAssessmentEpisodeDto(mapOf())

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        assertThatThrownBy { assessmentsService.updateEpisode(assessmentUuid, episodeUuid, updatedAnswers) }
                .isInstanceOf(EntityNotFoundException::class.java)
                .hasMessage("No Episode $episodeUuid for $assessmentUuid")
    }

    @Test
    fun `should add new answers to existing question for an episode`() {

        val answers = mutableMapOf(existingQuestionUuid to AnswerEntity(
                freeTextAnswer = "free text",
                answers = mapOf(existingAnswerUuid to "answer 1")))
        val assessment = assessmentEntity(answers)

        val newAnswerUUID = UUID.randomUUID()
        val updatedAnswers = UpdateAssessmentEpisodeDto(mapOf(existingQuestionUuid to AnswerDto(freeTextAnswer = "free text", answers = mapOf(existingAnswerUuid to "answer 1", newAnswerUUID to "answer 2"))))

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val episodeDto = assessmentsService.updateEpisode(assessmentUuid, episodeUuid, updatedAnswers)

        assertThat(episodeDto?.answers).hasSize(1)
        val answer = episodeDto?.answers?.get(existingQuestionUuid)
        assertThat(answer?.answers).hasSize(2)
        assertThat(answer?.answers).containsKey(existingAnswerUuid)
        assertThat(answer?.answers).containsKey(newAnswerUUID)
    }

    @Test
    fun `should change an existing answer for an episode`() {
        val answers = mutableMapOf(existingQuestionUuid to AnswerEntity(
                freeTextAnswer = "free text",
                answers = mapOf(existingAnswerUuid to "answer 1")))
        val assessment = assessmentEntity(answers)

        val updatedAnswers = UpdateAssessmentEpisodeDto(mapOf(existingQuestionUuid to AnswerDto(freeTextAnswer = "new free text", answers = mapOf(existingAnswerUuid to "new answer"))))

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val episodeDto = assessmentsService.updateEpisode(assessmentUuid, episodeUuid, updatedAnswers)

        assertThat(episodeDto?.answers).hasSize(1)
        val answer = episodeDto?.answers?.get(existingQuestionUuid)
        assertThat(answer?.answers).hasSize(1)
        assertThat(answer?.freeTextAnswer).isEqualTo("new free text")
        assertThat(answer?.answers).containsKey(existingAnswerUuid)
        assertThat(answer?.answers).containsValue("new answer")


    }

    @Test
    fun `should remove answers for an existing question for an episode`() {

        val answers = mutableMapOf(existingQuestionUuid to AnswerEntity(
                freeTextAnswer = "free text",
                answers = mapOf(existingAnswerUuid to "answer 1")))
        val assessment = assessmentEntity(answers)

        val newAnswerUUID = UUID.randomUUID()
        val updatedAnswers = UpdateAssessmentEpisodeDto(mapOf(existingQuestionUuid to AnswerDto(freeTextAnswer = "free text", answers = mapOf(newAnswerUUID to "answer 2"))))

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val episodeDto = assessmentsService.updateEpisode(assessmentUuid, episodeUuid, updatedAnswers)

        assertThat(episodeDto?.answers).hasSize(1)
        val answer = episodeDto?.answers?.get(existingQuestionUuid)
        assertThat(answer?.answers).hasSize(1)
        assertThat(answer?.answers).doesNotContainKey(existingAnswerUuid)
        assertThat(answer?.answers).containsKey(newAnswerUUID)
    }

    @Test
    fun `should retain existing questions and answers when not included in update`() {

        val answers = mutableMapOf(existingQuestionUuid to AnswerEntity(
                freeTextAnswer = "free text"))
        val assessment = assessmentEntity(answers)

        val newQuestionUUID = UUID.randomUUID()
        val newAnswerUUID = UUID.randomUUID()
        val updatedAnswers = UpdateAssessmentEpisodeDto(mapOf(newQuestionUUID to AnswerDto(freeTextAnswer = "new free text", answers = mapOf(newAnswerUUID to "answer 2"))))

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val episodeDto = assessmentsService.updateEpisode(assessmentUuid, episodeUuid, updatedAnswers)

        assertThat(episodeDto?.answers).hasSize(2)
        val existingAnswer = episodeDto?.answers?.get(existingQuestionUuid)
        assertThat(existingAnswer?.freeTextAnswer).isEqualTo("free text")

        val newAnswer = episodeDto?.answers?.get(newQuestionUUID)
        assertThat(newAnswer?.freeTextAnswer).isEqualTo("new free text")
        assertThat(newAnswer?.answers).containsKey(newAnswerUUID)
    }

    @Test
    fun `should not update a closed episode`() {

        val assessment = AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(
                                episodeUuid = episodeUuid,
                                episodeId = episodeId2,
                                endDate = LocalDateTime.now().minusDays(1),
                                changeReason = "Change of Circs 2",
                                answers = mutableMapOf(existingQuestionUuid to AnswerEntity(
                                        freeTextAnswer = "free text")),
                                assessment = AssessmentEntity(assessmentUuid = assessmentUuid)))
        )

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        assertThatThrownBy { assessmentsService.updateEpisode(assessmentUuid, episodeUuid, UpdateAssessmentEpisodeDto(answers = emptyMap())) }
                .isInstanceOf(UpdateClosedEpisodeException::class.java)
                .hasMessage("Cannot update closed Episode $episodeUuid for assessment $assessmentUuid")

    }

    @Test
    fun `should return answers for all episodes`() {

        setupQuestionCodes()
        setupAnswerCodes()

        val assessment = AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = episodeId1,
                                answers = mutableMapOf(question1Uuid to AnswerEntity(
                                        answers = mutableMapOf(answer1Uuid to "")))),
                        AssessmentEpisodeEntity(episodeId = episodeId2,
                                answers = mutableMapOf(question2Uuid to AnswerEntity(
                                        answers = mutableMapOf(answer2Uuid to ""))))))


        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)

        assertThat(result.assessmentUuid).isEqualTo(assessmentUuid)
        assertThat(result.answers["Q1"]?.first()?.answerSchemaCode).isEqualTo("A1")
        assertThat(result.answers["Q2"]?.first()?.answerSchemaCode).isEqualTo("A2")
    }


    @Test
    fun `should overwrite older episode answers with newer episode answers`() {
        setupQuestionCodes()
        setupAnswerCodes()

        val assessment = AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = episodeId1,
                                endDate = LocalDateTime.of(2020, 10, 1, 9, 0, 0),
                                answers = mutableMapOf(question1Uuid to AnswerEntity(
                                        answers = mutableMapOf(answer1Uuid to "")))),
                        AssessmentEpisodeEntity(episodeId = episodeId3,
                                endDate = LocalDateTime.of(2020, 10, 2, 10, 0, 0),
                                answers = mutableMapOf(question1Uuid to AnswerEntity(
                                        answers = mutableMapOf(answer3Uuid to "")))),
                        AssessmentEpisodeEntity(episodeId = episodeId2,
                                endDate = LocalDateTime.of(2020, 10, 2, 9, 0, 0),
                                answers = mutableMapOf(question1Uuid to AnswerEntity(
                                        answers = mutableMapOf(answer2Uuid to "")))),
                ))


        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
        val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)
        assertThat(result.answers["Q1"]?.first()?.answerSchemaCode).isEqualTo("A3")
    }

    @Test
    fun `should overwrite older episode answers latest episode answers`() {
        setupQuestionCodes()
        setupAnswerCodes()

        val assessment = AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = episodeId1,
                                endDate = LocalDateTime.of(2020, 10, 1, 9, 0, 0),
                                answers = mutableMapOf(question1Uuid to AnswerEntity(
                                        answers = mutableMapOf(answer1Uuid to "")))),
                        AssessmentEpisodeEntity(episodeId = episodeId3,
                                endDate = null,
                                answers = mutableMapOf(question1Uuid to AnswerEntity(
                                        answers = mutableMapOf(answer3Uuid to "")))),
                        AssessmentEpisodeEntity(episodeId = episodeId2,
                                endDate = LocalDateTime.of(2020, 10, 2, 9, 0, 0),
                                answers = mutableMapOf(question1Uuid to AnswerEntity(
                                        answers = mutableMapOf(answer2Uuid to "")))),
                ))

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment

        val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)
              assertThat(result.answers["Q1"]?.first()?.answerSchemaCode).isEqualTo("A3")

    }

    @Test
    fun `should not return free text answers`() {

        setupQuestionCodes()
        setupAnswerCodes()

        val assessment = AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = episodeId1,
                                answers = mutableMapOf(
                                        question1Uuid to AnswerEntity(answers = mutableMapOf(answer1Uuid to "")),
                                        question2Uuid to AnswerEntity(freeTextAnswer = "free text")))))


        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
        val result = assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid)

        assertThat(result.assessmentUuid).isEqualTo(assessmentUuid)
        assertThat(result.answers["Q1"]?.first()?.answerSchemaCode).isEqualTo("A1")
        assertThat(result.answers).doesNotContainKey("Q2")
    }


    @Test
    fun `should throw exception when question code lookup fails`() {
        every { questionService.getAllQuestions() } returns listOf(
                QuestionSchemaEntity(questionSchemaId = 2, questionSchemaUuid = question2Uuid, questionCode = "Q2")
        )

        every { questionService.getAllAnswers() } returns listOf()

        val assessment = AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = episodeId1,
                                answers = mutableMapOf(
                                        question1Uuid to AnswerEntity(answers = mutableMapOf(answer1Uuid to ""))))))

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
        assertThatThrownBy { assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid) }
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessage("Question Code not found for UUID $question1Uuid")

    }

    @Test
    fun `should throw exception when answer code lookup fails`() {
        every { questionService.getAllQuestions() } returns listOf(
                QuestionSchemaEntity(questionSchemaId = 1, questionSchemaUuid = question1Uuid, questionCode = "Q1")
        )

        every { questionService.getAllAnswers() } returns listOf()

        val assessment = AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(episodeId = episodeId1,
                                answers = mutableMapOf(
                                        question1Uuid to AnswerEntity(answers = mutableMapOf(answer1Uuid to ""))))))

        every { assessmentRepository.findByAssessmentUuid(assessmentUuid) } returns assessment
        assertThatThrownBy { assessmentsService.getCurrentAssessmentCodedAnswers(assessmentUuid) }
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessage("Answer Code not found for UUID $answer1Uuid")

    }

    private fun setupAnswerCodes() {
        println("answer1Uuid: $answer1Uuid, answer2Uuid: $answer2Uuid, answer3Uuid: $answer3Uuid,")
        every { questionService.getAllAnswers() } returns listOf(
                AnswerSchemaEntity(answerSchemaId = 1, answerSchemaUuid = answer1Uuid, answerSchemaCode = "A1", answerSchemaGroup = AnswerSchemaGroupEntity(answerSchemaId = 1, answerSchemaGroupUuid = UUID.randomUUID())),
                AnswerSchemaEntity(answerSchemaId = 2, answerSchemaUuid = answer2Uuid, answerSchemaCode = "A2", answerSchemaGroup = AnswerSchemaGroupEntity(answerSchemaId = 2, answerSchemaGroupUuid = UUID.randomUUID())),
                AnswerSchemaEntity(answerSchemaId = 3, answerSchemaUuid = answer3Uuid, answerSchemaCode = "A3", answerSchemaGroup = AnswerSchemaGroupEntity(answerSchemaId = 2, answerSchemaGroupUuid = UUID.randomUUID())),
        )
    }

    private fun setupQuestionCodes() {
        every { questionService.getAllQuestions() } returns listOf(
                QuestionSchemaEntity(questionSchemaId = 1, questionSchemaUuid = question1Uuid, questionCode = "Q1"),
                QuestionSchemaEntity(questionSchemaId = 2, questionSchemaUuid = question2Uuid, questionCode = "Q2")
        )
    }

    private fun assessmentEntity(answers: MutableMap<UUID, AnswerEntity>): AssessmentEntity {
        return AssessmentEntity(assessmentId = assessmentId,
                episodes = mutableListOf(
                        AssessmentEpisodeEntity(
                                episodeUuid = episodeUuid,
                                episodeId = episodeId2,
                                changeReason = "Change of Circs 2",
                                answers = answers))
        )
    }
}