package uk.gov.justice.digital.assessments.config

object CacheConstants {

  const val ASSESSMENT_SCHEMA_CACHE_KEY: String = "AssessmentSchemaService:assessmentSchema"
  const val QUESTIONS_FOR_SCHEMA_CODE_CACHE_KEY: String = "AssessmentSchemaService:questionsForSchemaCode"
  const val ASSESSMENT_SCHEMA_SUMMARY_CACHE_KEY: String = "AssessmentSchemaService:assessmentSchemaSummary"
  const val QUESTION_SCHEMA_CACHE_KEY: String = "QuestionService:questionSchema"
  const val LIST_GROUP_CACHE_KEY: String = "QuestionService:listGroups"
  const val GROUP_CONTENTS_CACHE_KEY: String = "QuestionService:getGroupContents"
  const val GROUP_SECTIONS_CACHE_KEY: String = "QuestionService:getGroupSections"
}
