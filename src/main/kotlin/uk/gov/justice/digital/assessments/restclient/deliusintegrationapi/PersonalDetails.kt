package uk.gov.justice.digital.assessments.restclient.deliusintegrationapi

data class PersonalDetails(
  val crn: String,
  val personalCircumstances: List<PersonalCircumstance>,
  val personalContacts: List<PersonalContact>
)
