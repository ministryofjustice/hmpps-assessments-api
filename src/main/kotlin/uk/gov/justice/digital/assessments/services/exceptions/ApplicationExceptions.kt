package uk.gov.justice.digital.assessments.services.exceptions

import org.springframework.http.HttpMethod
import uk.gov.justice.digital.assessments.restclient.ExternalService
import uk.gov.justice.digital.assessments.services.exceptions.ExceptionReason.DUPLICATE_OFFENDER_RECORD

enum class ExceptionReason {
  DUPLICATE_OFFENDER_RECORD,
  LAO_PERMISSION
}

// Internal Service Exceptions
class UserNotAuthorisedException(msg: String?, val extraInfoMessage: String?) : RuntimeException(msg)
class UpdateClosedEpisodeException(msg: String?) : RuntimeException(msg)
class EntityNotFoundException(msg: String?) : RuntimeException(msg)
class UserAreaHeaderIsMandatoryException(msg: String?) : RuntimeException(msg)
class MdcPropertyException(msg: String?) : RuntimeException(msg)
class MultipleExternalSourcesException(msg: String?) : RuntimeException(msg)
class CrnIsMandatoryException(msg: String?) : RuntimeException(msg)
class AuditFailureException(msg: String?) : RuntimeException(msg)

class DuplicateOffenderRecordException(
  msg: String?,
  val extraInfoMessage: String?,
  val reason: ExceptionReason = DUPLICATE_OFFENDER_RECORD
) : RuntimeException(msg)

class CannotCloseEpisodeException(
  msg: String?,
  val extraInfoMessage: String? = null,
) : RuntimeException(msg)

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
  val client: ExternalService,
  val moreInfo: List<String> = emptyList(),
  val reason: ExceptionReason? = null
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
