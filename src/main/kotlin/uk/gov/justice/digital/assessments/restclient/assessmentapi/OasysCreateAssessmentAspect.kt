package uk.gov.justice.digital.assessments.restclient.assessmentapi

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType

@Aspect
@Component
class OasysCreateAssessmentAspect() {

  @Around("execution(* uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient.createAssessment(..))")
  @Throws(Throwable::class)
  fun logAroundAllMethods(joinPoint: ProceedingJoinPoint): Long? {
    val signature: MethodSignature = joinPoint.signature as MethodSignature
    val oasysAssessmentType =
      if (signature.parameterNames.contains("oasysAssessmentType")) joinPoint.args[1] as OasysAssessmentType else null
    println("Trying to  ${joinPoint.signature.name} for oasysAssessmentType $oasysAssessmentType")
    if (shouldCreateAssessment(oasysAssessmentType)) {
      println("${joinPoint.signature.name} for oasysAssessmentType $oasysAssessmentType")
      return joinPoint.proceed() as Long?
    }
    return null
  }

  private fun shouldCreateAssessment(oasysAssessmentType: OasysAssessmentType?): Boolean {
    return oasysAssessmentType?.equals(OasysAssessmentType.SHORT_FORM_PSR) == true
  }
}
