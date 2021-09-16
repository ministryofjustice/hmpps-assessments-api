package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.OasysAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.services.dto.OasysAnswers
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.LocalDateTime
import java.util.UUID

@SqlGroup(
  Sql(
    scripts = ["classpath:assessments/before-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
  ),
  Sql(
    scripts = ["classpath:assessments/after-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
  )
)
@AutoConfigureWebTestClient(timeout = "600000")
class OasysAssessmentsControllerTest : IntegrationTest() {

  @Test
  fun `get latest closed episode rsr oasys answers`() {
    val crn = "X1355"

    val latestClosedEpisode = webTestClient.get().uri("/subject/$crn/assessments/episodes/RSR/current")
      .headers(setAuthorisation(roles = listOf("ROLE_ARN_READ_ONLY")))
      .exchange()
      .expectStatus().isOk
      .expectBody<OasysAssessmentEpisodeDto>()
      .returnResult()
      .responseBody

    Assertions.assertThat(latestClosedEpisode).isEqualTo(
      OasysAssessmentEpisodeDto(
        episodeUuid = UUID.fromString("f7765470-efd5-4589-8fdd-4570360e5289"),
        assessmentUuid = UUID.fromString("49c8d211-68dc-4692-a6e2-d58468127356"),
        created = LocalDateTime.of(2019, 11, 14, 9, 0),
        ended = LocalDateTime.of(2019, 11, 14, 13, 0),
        answers = OasysAnswers(
          mutableSetOf(
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.1.2_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.33",
              answer = "11/12/2020",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.13.1_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.2.2_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.40",
              answer = "8",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "6.7",
              answer = "YES",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.29",
              answer = "18/12/2020",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.10.1_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.34",
              answer = "5",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "12.1",
              answer = "1",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.35",
              answer = "3",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.7.2_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.9.2_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.8.2_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.12.2_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.13.2_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "9.2",
              answer = "2",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.10.2_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.39",
              answer = "YES",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "9.1",
              answer = "2",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.30",
              answer = "YES",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "11.2",
              answer = "1",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.ROSH.name,
              logicalPage = null,
              questionCode = "R1.2.6.2_V2",
              answer = "YES",
              isStatic = false
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.38",
              answer = "01/11/2021",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.32",
              answer = "10",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "4.2",
              answer = "0",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.36",
              answer = "2",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.37",
              answer = "2",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "6.7.1",
              answer = "perpetrator",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.8.2",
              answer = "01/01/2020",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.42",
              answer = "YES",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "6.4",
              answer = "2",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "11.4",
              answer = "2",
              isStatic = true
            ),
            OasysAnswer(
              sectionCode = AssessmentSchemaCode.RSR.name,
              logicalPage = null,
              questionCode = "1.41",
              answer = "YES",
              isStatic = true
            )
          )
        )
      )
    )
  }
}
