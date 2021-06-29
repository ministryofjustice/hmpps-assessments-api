package uk.gov.justice.digital.assessments.restclient.assessmentapi

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Authorized(val roleChecks: Array<Roles> = [], val roleNames: Array<RoleNames> = [])
