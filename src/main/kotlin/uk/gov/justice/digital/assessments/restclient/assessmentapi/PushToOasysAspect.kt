package uk.gov.justice.digital.assessments.restclient.assessmentapi

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode

@Aspect
@Component
class PushToOasysAspect() {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Around("execution(* uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient.pushToOasys(..))")
  @Throws(Throwable::class)
  fun aroundPushToOasysMethods(joinPoint: ProceedingJoinPoint): Pair<Long?, Long?> {
    val signature: MethodSignature = joinPoint.signature as MethodSignature
    val assessmentSchemaCode =
      if (signature.parameterNames.contains("assessmentSchemaCode")) joinPoint.args[2] as AssessmentSchemaCode else null

    log.info("Trying to  ${joinPoint.signature.name} for assessmentSchemaCode $assessmentSchemaCode")
    if (shouldCreateAssessment(assessmentSchemaCode)) {
      log.info("${joinPoint.signature.name} for assessmentSchemaCode $assessmentSchemaCode")
      return joinPoint.proceed() as Pair<Long?, Long?>
    }
    log.info("Assessment and Offender for $assessmentSchemaCode not pushed to Oasys")
    return Pair(null, null)
  }

  private fun shouldCreateAssessment(assessmentSchemaCode: AssessmentSchemaCode?): Boolean {
    return assessmentSchemaCode?.equals(AssessmentSchemaCode.ROSH) == true
  }
}
