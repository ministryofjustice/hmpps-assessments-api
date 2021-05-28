package uk.gov.justice.digital.assessments.services.exceptions

import org.springframework.http.HttpMethod
import uk.gov.justice.digital.assessments.restclient.ExternalService

// Internal Service Exceptions
class UserNotAuthorisedException(msg: String?) : RuntimeException(msg)
class UpdateClosedEpisodeException(msg: String?) : RuntimeException(msg)
class DuplicateOffenderRecordException(msg: String?) : RuntimeException(msg)
class EntityNotFoundException(msg: String?) : RuntimeException(msg)

// External Services Exceptions
class ExternalApiEntityNotFoundException(
  msg: String,
  val method: HttpMethod,
  val url: String,
  val client: ExternalService
) : RuntimeException(msg)

class ExternalApiAuthorisationException(
  msg: String,
  val method: HttpMethod,
  val url: String,
  val client: ExternalService
) : RuntimeException(msg)

class ExternalApiForbiddenException(
  msg: String,
  val method: HttpMethod,
  val url: String,
  val client: ExternalService
) : RuntimeException(msg)

class ExternalApiInvalidRequestException(
  msg: String,
  val method: HttpMethod,
  val url: String,
  val client: ExternalService
) : RuntimeException(msg)

class ExternalApiUnknownException(
  msg: String,
  val method: HttpMethod,
  val url: String,
  val client: ExternalService
) : RuntimeException(msg)
