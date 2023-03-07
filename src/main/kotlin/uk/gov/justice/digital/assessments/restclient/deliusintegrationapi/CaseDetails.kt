package uk.gov.justice.digital.assessments.restclient.deliusintegrationapi

import java.time.LocalDate

data class CaseDetails(
  val crn: String,
  val name: Name,
  val dateOfBirth: LocalDate,
  val gender: String? = null,
  val genderIdentity: String? = null,
  val croNumber: String? = null,
  val pncNumber: String? = null,
  val aliases: List<Alias>? = listOf(),
  val emailAddress: String? = null,
  val phoneNumbers: List<PhoneNumber>? = listOf(),
  val mainAddress: Address? = null,
  val ethnicity: String? = null,
  val disabilities: List<Disability>? = listOf(),
  val language: Language? = null,
  val personalCircumstances: List<PersonalCircumstance>? = listOf(),
  val personalContacts: List<PersonalContact>? = listOf(),
  val mappaRegistration: MappaRegistration? = null,
  val registerFlags: List<RegisterFlag>? = listOf(),
  val sentence: Sentence? = null

)

data class Alias(
  val name: Name,
  val dateOfBirth: LocalDate
)

data class PhoneNumber(
  val type: String,
  val number: String
)

data class Disability(
  val type: Type,
  val provisions: List<String>? = null,
  val notes: String
)
data class Language(
  val requiresInterpreter: Boolean = false,
  val primaryLanguage: String = "",
)

data class MappaRegistration(
  val startDate: LocalDate,
  val level: Type,
  val category: Type
)

data class RegisterFlag(
  val code: String,
  val description: String,
  val riskColour: String? = null
)

data class Sentence(
  val startDate: LocalDate,
  val mainOffence: MainOffence,
)

data class MainOffence(
  val category: Type,
  val subCategory: Type
)

data class Address(
  val buildingName: String? = null,
  val addressNumber: String? = null,
  val streetName: String? = null,
  val district: String? = null,
  val town: String? = null,
  val county: String? = null,
  val postcode: String? = null
)

data class PersonalCircumstance(
  val type: Type,
  val subType: Type? = null,
  val notes: String,
  val evidenced: Boolean,
)

data class PersonalContact(
  val relationship: String,
  val name: Name,
  val telephoneNumber: String? = null,
  val mobileNumber: String? = null,
  val address: Address?,
)

data class Type(
  val code: String,
  val description: String,
)

data class Name(
  val forename: String,
  val middleName: String? = null,
  val surname: String,
)
