package uk.gov.justice.digital.assessments.config

object CacheConstants {

  const val ASSESSMENT_CACHE_KEY: String = "AssessmentSchemaService:assessment"
  const val QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY: String = "AssessmentSchemaService:questionsForAssessmentType"
  const val ASSESSMENT_SUMMARY_CACHE_KEY: String = "AssessmentSchemaService:assessmentSummary"
  const val QUESTION_CACHE_KEY: String = "QuestionService:question"
  const val LIST_QUESTION_GROUPS_CACHE_KEY: String = "QuestionService:listQuestionGroups"
  const val QUESTION_GROUP_CONTENTS_CACHE_KEY: String = "QuestionService:questionGroupContents"
  const val QUESTION_GROUP_SECTIONS_CACHE_KEY: String = "QuestionService:questionGroupSections"
}
