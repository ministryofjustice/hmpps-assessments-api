package uk.gov.justice.digital.assessments.restclient.assessmentapi

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.restclient.AssessmentApiRestClient
import java.lang.reflect.Method

@Aspect
@Component
class OasysRBACAuthorizationAspect(val assessmentApiRestClient: AssessmentApiRestClient) {

  @Before("@annotation(uk.gov.justice.digital.assessments.restclient.assessmentapi.Authorized)")
  fun before(joinPoint: JoinPoint) {
    val signature: MethodSignature = joinPoint.signature as MethodSignature
    val method: Method = signature.method
    val authorized: Authorized = method.getAnnotation(Authorized::class.java)
    assessmentApiRestClient.getOASysRBACPermissions(
      roleChecks = authorized.roleChecks.toSet(),
      offenderPk = if (signature.parameterNames.contains("offenderPK")) joinPoint.args[0] as Long? else null,
      assessmentType = if (signature.parameterNames.contains("assessmentType")) joinPoint.args[1] as AssessmentType else AssessmentType.SHORT_FORM_PSR,
      oasysSetPk = if (signature.parameterNames.contains("oasysSetPk")) joinPoint.args[2] as Long? else null,
      roleNames = authorized.roleNames.toSet()
    )
  }
}
