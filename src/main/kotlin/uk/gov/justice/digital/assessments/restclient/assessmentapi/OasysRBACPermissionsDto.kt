package uk.gov.justice.digital.assessments.restclient.assessmentapi

import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType

data class OASysRBACPermissionsDto(
  val userCode: String,
  val roleChecks: Set<Roles>,
  val area: String,
  val offenderPk: Long? = null,
  val oasysSetPk: Long? = null,
  val oasysAssessmentType: OasysAssessmentType? = null,
  val roleNames: Set<RoleNames>? = emptySet()
)

enum class RoleNames {
  CREATE_BASIC_ASSESSMENT,
  CREATE_FULL_ASSESSMENT,
  CREATE_STANDARD_ASSESSMENT,
  EDIT_SARA,
  EDIT_SIGN_AND_LOCK_THE_ASSESSMENT,
  OPEN_OFFENDER_RECORD,
  OPEN_SARA,
  CREATE_OFFENDER,
  CREATE_RISK_OF_HARM_ASSESSMENT
}

enum class Roles {
  RBAC_OTHER,
  ASSESSMENT_READ,
  ASSESSMENT_EDIT,
  RBAC_SARA_EDIT,
  RBAC_SARA_CREATE,
  OFF_ASSESSMENT_CREATE
}
