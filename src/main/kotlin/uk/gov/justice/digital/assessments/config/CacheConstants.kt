package uk.gov.justice.digital.assessments.config

object CacheConstants {
  const val ASSESSMENT_CACHE_KEY: String = "AssessmentService:assessment1"
  const val QUESTIONS_FOR_ASSESSMENT_TYPE_CACHE_KEY: String = "AssessmentService:questionsForAssessmentType1"
  const val ASSESSMENT_SUMMARY_CACHE_KEY: String = "AssessmentService:assessmentSummary1"
  const val QUESTION_CACHE_KEY: String = "QuestionService:getQuestion1"
  const val LIST_QUESTION_GROUPS_CACHE_KEY: String = "QuestionService:listGroups1"
  const val QUESTION_GROUP_CONTENTS_CACHE_KEY: String = "QuestionService:getGroupContents1"
  const val QUESTION_GROUP_SECTIONS_CACHE_KEY: String = "QuestionService:getGroupSections1"
}
