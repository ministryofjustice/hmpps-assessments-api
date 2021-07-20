package uk.gov.justice.digital.assessments.services

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.services.dto.AssessmentEpisodeUpdateErrors

@Aspect
@Component
class PushToOasysAspect() {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Around("execution(* uk.gov.justice.digital.assessments.services.OasysAssessmentUpdateService.createOasysAssessment(..))")
  @Throws(Throwable::class)
  fun aroundPushToOasysMethods(joinPoint: ProceedingJoinPoint): Pair<Long?, Long?> {
    val signature: MethodSignature = joinPoint.signature as MethodSignature
    val assessmentSchemaCode =
      if (signature.parameterNames.contains("assessmentSchemaCode")) joinPoint.args[2] as AssessmentSchemaCode else null

    log.info("Trying to  ${joinPoint.signature.name} for assessmentSchemaCode $assessmentSchemaCode")
    if (shouldPushToOasys(assessmentSchemaCode)) {
      log.info("${joinPoint.signature.name} for assessmentSchemaCode $assessmentSchemaCode")
      return joinPoint.proceed() as Pair<Long?, Long?>
    }
    log.info("Assessment and Offender for $assessmentSchemaCode not pushed to Oasys")
    return Pair(null, null)
  }

  @Pointcut("execution(* uk.gov.justice.digital.assessments.services.OasysAssessmentUpdateService.updateOASysAssessment(..))")
  fun pushUpdateToOasysPointcut() {
  }

  @Pointcut("execution(* uk.gov.justice.digital.assessments.services.OasysAssessmentUpdateService.completeOASysAssessment(..))")
  fun pushCompleteToOasysPointcut() {
  }

  @Around("pushUpdateToOasysPointcut() || pushCompleteToOasysPointcut()")
  @Throws(Throwable::class)
  fun aroundPushUpdateToOasysMethods(joinPoint: ProceedingJoinPoint): Any? {
    val signature: MethodSignature = joinPoint.signature as MethodSignature
    val assessmentEpisode =
      if (signature.parameterNames.contains("episode")) joinPoint.args[0] as AssessmentEpisodeEntity else null

    val assessmentSchemaCode = assessmentEpisode?.assessmentSchemaCode
    log.info("Trying to ${joinPoint.signature.name} for assessment episode ${assessmentEpisode?.episodeUuid} with assessmentSchemaCode $assessmentSchemaCode")
    if (shouldPushToOasys(assessmentSchemaCode)) {
      log.info("${joinPoint.signature.name} assessment episode ${assessmentEpisode?.episodeUuid} with assessmentSchemaCode $assessmentSchemaCode")
      return joinPoint.proceed()
    }
    log.info("${joinPoint.signature.name} assessment episode ${assessmentEpisode?.episodeUuid} with assessmentSchemaCode $assessmentSchemaCode not pushed to Oasys")
    return null
  }

  private fun shouldPushToOasys(assessmentSchemaCode: AssessmentSchemaCode?): Boolean {
    return assessmentSchemaCode?.equals(AssessmentSchemaCode.ROSH) == true
  }
}
