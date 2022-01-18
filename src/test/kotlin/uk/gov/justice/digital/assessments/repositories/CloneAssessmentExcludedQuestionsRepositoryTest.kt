package uk.gov.justice.digital.assessments.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode.UPW
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.CloneAssessmentExcludedQuestionsRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

class CloneAssessmentExcludedQuestionsRepositoryTest(
  @Autowired val cloneAssessmentExcludedQuestionsRepository: CloneAssessmentExcludedQuestionsRepository
) : IntegrationTest() {

  @Test
  fun `return excluded question entities by assessment schema code`() {
    val cloneAssessmentExcludedQuestions = cloneAssessmentExcludedQuestionsRepository.findAllByAssessmentSchemaCode(UPW)
    assertThat(cloneAssessmentExcludedQuestions.map { it.questionCode }).containsExactlyInAnyOrder(
      "individual_details_complete",
      "cultural_religious_adjustment_complete",
      "placement_preference_complete",
      "placement_preference_by_gender_complete",
      "maturity_assessment_details_complete",
      "rosh_community_complete",
      "managing_risk_complete",
      "disabilities_complete",
      "health_issues_complete",
      "gp_details_complete",
      "travel_information_complete",
      "caring_commitments_complete",
      "employment_education_skills_complete",
      "employment_training_complete",
      "eligibility_intensive_working_complete",
      "individual_availability_complete",
      "equipment_complete"
    )
  }
}
