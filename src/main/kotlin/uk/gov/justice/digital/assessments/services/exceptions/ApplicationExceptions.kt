package uk.gov.justice.digital.assessments.services.exceptions

class EntityNotFoundException(msg: String?) : RuntimeException(msg)
class UserNotAuthorisedException(msg: String?) : RuntimeException(msg)
class UpdateClosedEpisodeException(msg: String?) : RuntimeException(msg)
class DuplicateOffenderRecordException(msg: String?) : RuntimeException(msg)
