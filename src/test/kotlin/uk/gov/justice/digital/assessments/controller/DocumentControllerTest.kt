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
class DocumentControllerTest : IntegrationTest() {

  @Test
  fun `uploads a UPW document to community-api`() {
    val assessmentId = UUID.fromString("a1b45012-c2a2-40ee-bc25-77c61d6313a3")
    val episodeId = UUID.fromString("20152a2f-a787-4dbb-8013-59de52f639ea")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

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

  @Test
  fun `returns a 404 when unable to find the assessment`() {
    val assessmentId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
    val episodeId = UUID.fromString("d7aafe55-0cff-4f20-a57a-b66d79eb9c91")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .isNotFound
  }

  @Test
  fun `returns a 404 when unable to find the conviction for a subject`() {
    val assessmentId = UUID.fromString("9571fdcc-0d41-40e6-93df-c05c6bfbeb32")
    val episodeId = UUID.fromString("ca97390b-5bff-4e62-91be-e89f23137a71")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .isNotFound
  }

  @Test
  fun `returns a 404 when unable to find the conviction ID for a given index`() {
    val assessmentId = UUID.fromString("0ead1d46-274b-4e38-aefc-d054eb7cc1d5")
    val episodeId = UUID.fromString("659cd2a7-bfa7-41be-8ac1-dddc005928d3")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .isNotFound
  }

  @Test
  fun `returns a 400 error when returned whilst uploading the document`() {
    val assessmentId = UUID.fromString("f9cc22cc-bad6-4a0a-9679-3e0f0b65d844")
    val episodeId = UUID.fromString("9d5d5859-edfd-4f08-ae08-3c1e333dfb74")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .is5xxServerError
  }

  @Test
  fun `returns a 401 error when returned whilst uploading the document`() {
    val assessmentId = UUID.fromString("1e96b747-e2f3-42f6-aee7-faa0a79b640c")
    val episodeId = UUID.fromString("c2decc11-c26c-47a7-9eaa-5930bc3f034f")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .is5xxServerError
  }

  @Test
  fun `returns a 403 error when returned whilst uploading the document`() {
    val assessmentId = UUID.fromString("06973ac6-9e11-4f1f-8d75-55b44d38d85a")
    val episodeId = UUID.fromString("fcb5ce87-179c-4d07-8763-92af51d3e91c")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .is5xxServerError
  }

  @Test
  fun `returns a 404 error when returned whilst uploading the document`() {
    val assessmentId = UUID.fromString("4462d22f-8289-429c-8659-cd5342e71a38")
    val episodeId = UUID.fromString("4dc44a72-bbe3-41a7-887c-eac8efa2cac7")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .is5xxServerError
  }

  @Test
  fun `returns a 500 error when returned whilst uploading the document`() {
    val assessmentId = UUID.fromString("361cb438-2d97-4469-bab3-8b514cb987dc")
    val episodeId = UUID.fromString("f28c98d9-4363-4d51-8062-54f2054d5bd3")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .is5xxServerError
  }

  @Test
  fun `returns a 502 error when returned whilst uploading the document`() {
    val assessmentId = UUID.fromString("760a3a4c-c202-418d-9169-95226926b08e")
    val episodeId = UUID.fromString("5fea9e84-60bf-4d85-a230-7ad59a735a69")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .is5xxServerError
  }

  @Test
  fun `returns a 503 error when returned whilst uploading the document`() {
    val assessmentId = UUID.fromString("41073079-68c8-4bcb-b557-26ce1eb0ae8b")
    val episodeId = UUID.fromString("1d639456-6243-41c6-b43b-d08cff03edf8")
    val path = "/assessments/$assessmentId/episode/$episodeId/document"

    val multipartBodyBuilder = MultipartBodyBuilder()
    multipartBodyBuilder.part("fileData", multiPartFile().resource)

    webTestClient.post()
      .uri(path)
      .contentType(MediaType.MULTIPART_FORM_DATA)
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION", "ROLE_COMMUNITY")))
      .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
      .exchange()
      .expectStatus()
      .is5xxServerError
  }

  fun multiPartFile(): MultipartFile {
    return MockMultipartFile(
      "fileData",
      "filename",
      MediaType.TEXT_PLAIN_VALUE,
      "Test information contained in a document".toByteArray()
    )
  }
}
