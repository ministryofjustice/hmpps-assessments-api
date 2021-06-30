package uk.gov.justice.digital.assessments.restclient.assessmentapi

data class OASysRBACErrorResponse(
  val status: Int,
  val developerMessage: String? = null,
  val payload: RBACPermissionsPayload? = null,
)

data class RBACPermissionsPayload(
  val userCode: String,
  val offenderPk: Long,
  val permissions: List<RBACPermission>
)

data class RBACPermission(
  val checkCode: Roles,
  val authorised: Boolean,
  val returnMessage: String? = null
)
