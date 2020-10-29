package uk.gov.justice.digital.needs.controller

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient

import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.needs.api.CriminogenicNeedsDto
import uk.gov.justice.digital.assessments.controller.IntegrationTest
import java.util.UUID
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import uk.gov.justice.digital.assessments.services.AssessmentService
import uk.gov.justice.digital.assessments.api.AssessmentAnswersDto
import uk.gov.justice.digital.needs.api.CriminogenicNeed
import uk.gov.justice.digital.needs.api.NeedStatus
import org.assertj.core.api.Assertions.assertThat
import uk.gov.justice.digital.assessments.api.AnswerSchemaDto


@AutoConfigureWebTestClient
class CriminogenicNeedsControllerTest: IntegrationTest() {

    @MockkBean
    private lateinit var assessmentService: AssessmentService


    @Test
    fun `calculate Criminogenic Needs from questions and answers`() {
        val assessmentUuid = UUID.randomUUID()
        val assessmentAnswerDto = AssessmentAnswersDto(
                assessmentUuid = assessmentUuid,
                answers = mapOf("3.98" to setOf(AnswerSchemaDto(
                        answerSchemaUuid = UUID.randomUUID(),
                        answerSchemaCode =  "YES"))))
        every { assessmentService.getCurrentAssessmentCodedAnswers(assessmentUuid) } returns (assessmentAnswerDto)

        val result = webTestClient.get().uri("/assessments/$assessmentUuid/needs")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<CriminogenicNeedsDto>()
                .returnResult()
                .responseBody

        val need = result!!.criminogenicNeeds.toList().first { it.need == CriminogenicNeed.ACCOMMODATION }
        assertThat(need.riskOfHarm).isTrue()
        assertThat(need.lowScoringNeed).isNull()
        assertThat(need.overThreshold).isNull()
        assertThat(need.riskOfReoffending).isNull()
        assertThat(need.needStatus).isEqualTo(NeedStatus.NEED_IDENTIFIED)
    }
}