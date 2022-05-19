package uk.gov.justice.digital.assessments.config

object CacheConstants {

  const val ASSESSMENT_CACHE_KEY: String = "AssessmentService:assessment"
  const val QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY: String = "AssessmentService:questionsForAssessmentType"
  const val ASSESSMENT_SUMMARY_CACHE_KEY: String = "AssessmentService:assessmentSummary"
  const val QUESTION_CACHE_KEY: String = "QuestionService:getQuestion"
  const val LIST_QUESTION_GROUPS_CACHE_KEY: String = "QuestionService:listGroups"
  const val QUESTION_GROUP_CONTENTS_CACHE_KEY: String = "QuestionService:getGroupContents"
  const val QUESTION_GROUP_SECTIONS_CACHE_KEY: String = "QuestionService:getGroupSections"
}
