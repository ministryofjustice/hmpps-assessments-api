package uk.gov.justice.digital.assessments.services.exceptions

import org.springframework.http.HttpMethod
import uk.gov.justice.digital.assessments.restclient.ExternalService

// Internal Service Exceptions
class UserNotAuthorisedException(msg: String?) : RuntimeException(msg)
class UpdateClosedEpisodeException(msg: String?) : RuntimeException(msg)
class DuplicateOffenderRecordException(msg: String?) : RuntimeException(msg)
class EntityNotFoundException(msg: String?) : RuntimeException(msg)

// External Services Exceptions
class ApiClientEntityNotFoundException(
  msg: String,
  val method: HttpMethod,
  val url: String,
  val client: ExternalService
) : RuntimeException(msg)

class ApiClientAuthorisationException(
  msg: String,
  val method: HttpMethod,
  val url: String,
  val client: ExternalService
) : RuntimeException(msg)

class ApiClientForbiddenException(
  msg: String,
  val method: HttpMethod,
  val url: String,
  val client: ExternalService
) : RuntimeException(msg)

class ApiClientInvalidRequestException(
  msg: String,
  val method: HttpMethod,
  val url: String,
  val client: ExternalService,
  val extraParam: String?
) : RuntimeException(msg)

class ApiClientUnknownException(
  msg: String,
  val method: HttpMethod,
  val url: String,
  val client: ExternalService
) : RuntimeException(msg)
