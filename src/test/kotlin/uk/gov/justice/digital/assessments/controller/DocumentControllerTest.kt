package uk.gov.justice.digital.assessments.controller

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
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

@AutoConfigureWebTestClient(timeout = "1000000")
class DocumentControllerTest: IntegrationTest() {

  @Test
  fun `uploads a UPW document to community-api`() {

    val episodeId = UUID.fromString("2e020e78-a81c-407f-bc78-e5f284e237e5")
    val assessmentId = UUID.fromString("d7aafe55-0cff-4f20-a57a-b66d79eb9c91")
    val path = "assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .isOk
  }


  fun multiPartFile(): MultipartFile {
    return MockMultipartFile(
      "fileData",
      "filename",
      MediaType.TEXT_PLAIN_VALUE,
      "Test information contained in a document".toByteArray()
    )
  }

  //    val crn = "X1355"
//    val eventId = 1L
//    val convictionId = 623456L
}